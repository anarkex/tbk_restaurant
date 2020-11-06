Hola,

Esta es mi prueba para postular a Transbank,
el requerimiento fue:

1. Exponer endpoint de login, el cual debe aceptar un nombre y usuario y contraseña, las cuales deben ser almacenadas de manera segura, como es un ejemplo, no es necesario crear un api de creación de usuario y basta con dejar en el Readme las credenciales (nombre de usuario y contraseña) creado previamente para poder simular la llamada al login exitoso.
2. Luego de estar logueado en el punto 1, debe existir un api para crear ventas y otra que devuelva el listado de ventas del día, no está definido cuales campos debe incluir una venta, el objetivo de la prueba es evaluar cómo piensa el candidato a la hora de definir un api.
3. Se Debe considerar que la api que devuelve las ventas del día tienen un alto uso, por lo cual se debe implementar una cola con JMS solo a nivel de mock para simular un endpoint con carga excesiva.
4. Las herramientas, librerías y otros componentes de Spring son de uso libre, el candidato puede elegir cuales necesita o desee usar
5. Se debe finalmente proveer la documentación de la API construida, mediante alguna herramienta de libre elección destinada para estos propósitos, siguiendo el enfoque (bottom-up), swagger por ejemplo.

El desarrollo fue iniciado el Mie. 4 de Nov del 2020 a las 10pm +/-

Solucion
========
1. El sistema es un api json que tiene un /login con un payload { "username": "transbank", "password": "test" }.
- "el cual debe aceptar un nombre y usuario y contraseña": Asumo que dice "nombre DE usuario y contraseña" y que es sólo un nombre de usuario y una contraseña.
- "deben ser almacenadas de manera segura": Asumo que esto significa que deben estar en algun archivo de configuracion encriptados, los meti en application.properties encriptado con jasypt
El login devuelve la siguiente estructura:
{
    "username": "transbank",
    "password": null,
    "token": "eyJhb...eu9aNg",
    "tokenType": "Bearer"
}
Las llamadas a /ventas/** deben ser encabezadas con Authorization Bearer <token>
El /login está restringido a application.properties:auth.maxLoginAttempts intentos dentro de 30 segundos. Si el atacante intenta logearse de nuevo dentro de esos 30 segundos el tiempo se reinicia.
No le puse certificado SSL porque es sólo una prueba y esto va a estar en localhost, pero en un entorno real Debería tener SSL.

