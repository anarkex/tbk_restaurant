# Transbank Restaurant - prueba de postulación

Esta es mi prueba para postular a Transbank,

USUARIO: transbank

PASSWORD: test

URL: http://<host>:8080/tbk/restaurant/v1

Probado en Oracle/Sun Java 8

CMD: java -jar restaurant-1.0-SNAPSHOT.jar


el requerimiento fue:

1. Exponer endpoint de login, el cual debe aceptar un nombre y usuario y contraseña, las cuales deben ser almacenadas de manera segura, como es un ejemplo, no es necesario crear un api de creación de usuario y basta con dejar en el Readme las credenciales (nombre de usuario y contraseña) creado previamente para poder simular la llamada al login exitoso.
2. Luego de estar logueado en el punto 1, debe existir un api para crear ventas y otra que devuelva el listado de ventas del día, no está definido cuales campos debe incluir una venta, el objetivo de la prueba es evaluar cómo piensa el candidato a la hora de definir un api.
3. Se Debe considerar que la api que devuelve las ventas del día tienen un alto uso, por lo cual se debe implementar una cola con JMS solo a nivel de mock para simular un endpoint con carga excesiva.
4. Las herramientas, librerías y otros componentes de Spring son de uso libre, el candidato puede elegir cuales necesita o desee usar
5. Se debe finalmente proveer la documentación de la API construida, mediante alguna herramienta de libre elección destinada para estos propósitos, siguiendo el enfoque (bottom-up), swagger por ejemplo.

El desarrollo fue iniciado el Mie. 4 de Nov del 2020 a las 10pm +/-. Este desarrollo está pensado como un primer ciclo en el SDLC esperando feedback.

## Flujo de la solucion (historia)
1. El usuario envía su username y password al endpoint de login ( POST /login )
    - el endpoint del login responde con un usuario, token y tipo de token, hoy el token es Bearer (JWT)
2. El usuario envía los datos de una venta al endpoint de agregar ventas ( POST /ventas/add ) además del header `Authorization Bearer` con el token recuperado en 1
    - el endpoint devuelve los datos de la venta almacenada
3. El usuario envía una petición al endpoint de resumen diario de ventas con el día que quiere revisar ( POST /ventas/resume/2000/5/24 ) además del header `Authorization Bearer` con el token recuperado en 1
    - el endpoint recopila los datos y genera un resumen diario de ventas

### Excepciones
1. El usuario envía su username y password equivocados al login ( POST /login )
    - el sistema devuelve el error `HTTP 401 Unauthorized`
2. El usuario envia una peticion mal formada al login ( POST /login )
    - el sistema devuelve el error `HTTP 400 Bad Request`
3. El usuario envia una peticion de agregar venta mal formada ( POST /ventas/add )
    - el sistema devuelve un `HTTP 400 Bad Request`
4. El usuario envía una peticion de agregar venta sin el token o con el token errado ( POST /ventas/add )
    - el sistema devuelve un `HTTP 403 Forbidden`
5. El usuario envía una peticion de solicitud de resumen con una fecha errada ( POST /ventas/resume/2020/01/723 )
    - el sistema interpreta esa fecha y devuelve el reporte para la fecha interpretada (723 dias desde el 1 de enero del 2020 para la fecha solicitada)
6. El usuario envía una peticion de solicitid de resumen para una fecha sin ventas ( POST /ventas/resume/2030/01/01 )
    - el sistema devuelve un resumen sin ventas con cuenta de montos, items y ventas en 0.
7. El usuario envía una peticion de solicitud de resumen sin el token o con el token errado ( POST /ventas/resume/2020/10/23 )
    - el sistema devuelve un `HTTP 403 Forbidden`

## Solución

### 1. ***Exponer el endpoint del login, el cual debe aceptar un ~~nombre y~~ usuario y contraseña, las cuales deben ser almacenadas de manera segura, como es un ejemplo, no es necesario crear una api de creación de usuario y basta con dejar en el Readme las credenciales (nombre de usuario y contraseña) creada previamente para poder simular al llamada al login exitoso.***
 El sistema es un api json que tiene un /login con un payload `{ "username": "transbank", "password": "test" }` .

"el cual debe aceptar un nombre y usuario y contraseña" : Asumo que dice "nombre DE usuario y contraseña" y que es sólo un nombre de usuario y una contraseña.

"deben ser almacenadas de manera segura": Asumo que esto significa que deben estar en algun archivo de configuracion encriptados, los meti en `application.properties` encriptado con jasypt
El nombre de usuario es "transbank" y la clave es "test" y la clave de jasypt es "tbk"

El login devuelve la siguiente estructura:
```
{
    "username": "transbank",
    "password": null,
    "token": "eyJhb...eu9aNg",
    "tokenType": "Bearer"
}
``` 
Las llamadas a `/ventas/**` deben ser encabezadas con `Authorization Bearer <token>`

El `/login` está restringido a `application.properties:${auth.maxLoginAttempts}` intentos dentro de 30 segundos. Si el atacante intenta logearse de nuevo dentro de esos 30 segundos el tiempo se reinicia. La restricción está hacia el usuario, no hacia la IP.

No le puse certificado SSL al servidor que levanta el jar porque es sólo una prueba y esto va a estar en localhost, pero en un entorno real Debería tener SSL. En mi máquina de pruebas si tiene SSL porque está detrás de un proxy reverso.

### 2. "Api para crear ventas y otra que devuelve el listado de ventas del dia"
- `/ventas/resume/{año}/{mes}/{dia}` : obtiene un objeto JSON de resumen de la venta
- `/ventas/add` : se le entrega un objeto venta para guardarlo. Si el objeto tiene el mismo id que alguno que ya estaba en la base, simplemente lo pisa. Si la venta va sin fecha, el sistema le pone la fecha. También como auditoría guarda el usuario que subió la venta (que en estos casos es el mismo).
En el archivo `src/main/resources/swagger.yml` aparece como llamar a estos servicios.
- La venta registra id (generado en el cliente usando UUID, es poco probable que se repita, pero no imposible), una fecha de venta (que es cuando se inserta), un id de sucursal (que es sólo un id que podría estar referenciado en los códigos de sucursal que se le entregagron al SII), el usuario que inyectó esta venta en el sistema y un listado de items (sin asegurar orden).
- El ítem registra su ID de item, un código de producto (que en mi idea es el código de barras del producto), la cantidad, el precio unitario y el detalle.
- El resumen de venta tiene una fecha de generado, una fecha de ventas, la cuenta de los items, la cuenta de las ventas del dia, el monto total y un listado de ventas sin asegurar orden. El resumen no se guarda en la base, es generado a partir de los datos de la base de datos cada vez que alguien lo requiere.

### 3. "ventas del día tiene alto uso --> JMS"
- No entendí bien que hay que hacer aqui, pero... hice servicios de persistencia y recuperación con *Hazelcast* y otra interfaz con *JMS*. 

- *Hazelcast* : No lo he porbado con múltiples nodos, pero se supone que podria subir varias instancias del sistema en distintas maquinas (bajo la misma máscara de subred, pq funciona esn multicast) y todas funcionarian juntas. En condiciones normales cada uno de esos nodos tendria acceso a la base, pero en estas condiciones de test todos los nodos levantan una instancia h2, pero solo un nodo (el primero) trabaja como la base de datos guardando y recuperando datos usando una Queue. 

La ventaja con Hazelcast es que maneja un caché en memoria distribuída de forma que si piden un resumen de ventas con espacios de menos de 5 minutos (no recuerdo cuanto le puse) el reporte se mantiene en el caché por otros 5 minutos hasta que ya nadie lo pide y se pierde, cuando se perdió y lo solicitan de nuevo el sistema lo toma de la base y lo deja disponible de nuevo. También si el reporte está en caché e ingresan una nueva venta el sistema va a refrescar el reporte si es que estba en el caché.

Los resumenes se mantienen en caché indexados por fecha de ventas.

En mi implementación de este sistema la generación de resumenes postea una fecha para resumen y luego se queda esperando a que aparezca en el caché de resúmenes, si demora más de 5 segundos ( `application.prperties:${resumeTimeout}` ) la petición falla por timeout.

- *JMS* : ActiveMQ + SpringJMS ...  
Este no tiene un caché de reportes (no se si se pueda) y cada peticion de resumen es una petición nueva. Si piden 15 resúmenes para el mismo día cada resumen se genera nuevamente desde la base y se deja disponible en la cola de recepción. No me gusta la idea de persistir estos reportes porque si aparece una venta hay que refrescarlo, lo que se podría implementar es un Store procedure para generarlos en la base (no se si h2 me deja hacer eso).

***Si me preguntan a mi, yo creo que Hazelcast va a ser bastante mas rápido, eficiente y escalable que JMS***

### 4. "Librerias" 
Spring, ActiveMQ, Hazelcast, H2, Hibernate, Jasypt, jsonwebtoken.

Hibernate no es de mi completo agrado porque me hace perder el control en un factor crítico como es la creación de queries optimizadas para la base, pero esto es un test.

### 5. Documentacion: 
Dejé un archivo `src/main/resources/swagger.yml` en el repo. Está configurado para funcionar contra una máquina activa: https://comprando.cl/tbk

Y este `readme.md` para dar una pincelada de lo que hay adentro.

También dejé un export de postman para enviar peticiones.

### Este sistema necesita testing intenso. Sólo se crearon JUnit para probarlo (se ejecuta durante el build de maven).

## ISSUES Conocidos y comentarios:
- No va a iniciar varias veces en el mismo servidor por conflicto de puertos de activemq y h2
- la base de datos h2 crea su datafile en el lugar donde el usuario haya levantado la aplicacion
- Un acceso sin token a un servicio resulta en un error `403 Forbidden` en vez del que corresponde `401 Unauthorized` .
- a veces demora bastante en levantar
- Hay clases que no estoy seguro si deberían llamarse así o de otra forma, lo que si se es que necesita refactorizacion
- A causa de que hoy en día todos usamos pantallas de 16:9 y nadie imprime el código en matriz de puntos es que no seguí ningúna regla de 80 caracteres de ancho.
- El sistema esta en blanco, debes subir un par de ventas para un día en específico y luego pedir el resumen diario de ese día
- Cambiar entre `Hazelcast` y `JMS`, para la generación del reporte, se hace cambiando el `@Qualifier` de `RestaurantService` en el `VentaController`
- Esto necesita una pulida en cuanto a que dependencias son realmente necesarias para hacer el paquete mas pequeño
- No tengo mucha experiencia configurando JMS/ActiveMQ, antes de esto lo había usado como POC en Glassfish 3 (hace años).
- No soy un experto con Hazelcast, pero me gusta el approach que tiene para distribuir.
- Swagger fue algo que no conocía