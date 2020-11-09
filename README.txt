Hola,

USUARIO: transbank
PASSWORD: test

Esta es mi prueba para postular a Transbank,
el requerimiento fue:

1. Exponer endpoint de login, el cual debe aceptar un nombre y usuario y contraseña, las cuales deben ser almacenadas de manera segura, como es un ejemplo, no es necesario crear un api de creación de usuario y basta con dejar en el Readme las credenciales (nombre de usuario y contraseña) creado previamente para poder simular la llamada al login exitoso.
2. Luego de estar logueado en el punto 1, debe existir un api para crear ventas y otra que devuelva el listado de ventas del día, no está definido cuales campos debe incluir una venta, el objetivo de la prueba es evaluar cómo piensa el candidato a la hora de definir un api.
3. Se Debe considerar que la api que devuelve las ventas del día tienen un alto uso, por lo cual se debe implementar una cola con JMS solo a nivel de mock para simular un endpoint con carga excesiva.
4. Las herramientas, librerías y otros componentes de Spring son de uso libre, el candidato puede elegir cuales necesita o desee usar
5. Se debe finalmente proveer la documentación de la API construida, mediante alguna herramienta de libre elección destinada para estos propósitos, siguiendo el enfoque (bottom-up), swagger por ejemplo.

El desarrollo fue iniciado el Mie. 4 de Nov del 2020 a las 10pm +/-

Solución
========
1. El sistema es un api json que tiene un /login con un payload { "username": "transbank", "password": "test" }.
- "el cual debe aceptar un nombre y usuario y contraseña": Asumo que dice "nombre DE usuario y contraseña" y que es sólo un nombre de usuario y una contraseña.
- "deben ser almacenadas de manera segura": Asumo que esto significa que deben estar en algun archivo de configuracion encriptados, los meti en application.properties encriptado con jasypt
El nombre de usuario es "transbank" y la clave es "test"

El login devuelve la siguiente estructura:
{
    "username": "transbank",
    "password": null,
    "token": "eyJhb...eu9aNg",
    "tokenType": "Bearer"
}
Las llamadas a /ventas/** deben ser encabezadas con Authorization Bearer <token>
El /login está restringido a ${application.properties:auth.maxLoginAttempts} intentos dentro de 30 segundos. Si el atacante intenta logearse de nuevo dentro de esos 30 segundos el tiempo se reinicia.
No le puse certificado SSL porque es sólo una prueba y esto va a estar en localhost, pero en un entorno real Debería tener SSL.

2. "Api para crear ventas y otra que devuelve el listado de ventas del dia"
- /ventas/resume/{año}/{mes}/{dia} : obtiene un objeto JSON de resumen de la venta
- /ventas/add : se le entrega un objeto venta para guardarlo. Si el objeto tiene el mismo id que alguno que ya estaba en la base, simplemente lo pisa. Si la venta va sin fecha, el sistema le pone la fecha. También como auditoría guarda el usuario que subió la venta (que en estos casos es el mismo).

3."ventas del día tiene alto uso --> JMS"
- No entendí bien que hay que hacer aqui, pero... hice una interfaz con Hazelcast y otra interfaz con JMS. 
Hazelcast: No lo he porbado pero se supone que, con hazelcast, podria subir varias instancias de el sistema en distintas maquinas (bajo el mismo router pq esta configurado para funcionar con multicast) y todas funcionarian juntas. En condiciones normales cada uno de esos nodos tendria acceso a la base, en estas condiciones todos los nodos levantan una instancia h2, pero solo un nodo (el primero) trabaja como la base de datos guardando y recuperando datos.
JMS: ActiveMQ + SpringJMS ...  
- Si me preguntan a mi, yo creo que Hazelcast va a ser bastante mas rápido, eficiente y escalable que JMS

4. "Librerias": Spring, ActiveMQ, Hazelcast, H2, Jasypt, jsonwebtoken.
5. Documentacion: Dejé un archivo swagger.yml en el directorio. Está configurado para funcionar contra una máquina activa: https://comprando.cl/tbk
   También dejé un export de postman

====
ISSUES Conocidos:
- No va a iniciar varias veces en el mismo servidor por conflicto de puertos de activemq y h2
- Un acceso sin token a un servicio resulta en un error 403 Forbidden en vez del que corresponde 401 Unauthorized.
- demora mucho en levantar
- Hay clases que no estoy seguro si deberían llamarse así o de otra forma, lo que si se es que necesita refactorizacion
- A causa de que hoy en día todos usamos pantallas de 16:9 y nadie imprime el código en matriz de puntos es que no seguí ningúna regla de 80 caracteres de ancho.