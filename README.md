📌 Descripción
Este es el servicio principal del sistema de gestión de turnos. Expone endpoints REST para crear, eliminar y atender tickets, además de procesarlos mediante colas de RabbitMQ.

🧱 Contenido
TicketResource: expone endpoints REST

POST /tickets: Encola ticket vía RabbitMQ

DELETE /tickets/{id}: Solicita cancelación de ticket

GET /tickets/next?service=...: Atiende siguiente ticket por servicio

TicketConsumer: recibe mensajes desde RabbitMQ y ejecuta operaciones

PersistenceService: guarda automáticamente en base de datos cuando se atiende o elimina un ticket

Inyección de dependencias para estructuras en memoria (MyBinaryTree, MyCustomerList, MyStack)

⚙️ Tecnologías
Java 17

Quarkus

JPA / Hibernate

RabbitMQ con SmallRye Reactive Messaging

RESTEasy Reactive

Docker (para contenedor RabbitMQ)

🧠 Lógica Aplicada
Tickets se insertan en un árbol binario por tipo de servicio.

Cada nodo del árbol tiene una cola FIFO con los tickets pendientes.

Al atender un ticket, se registra en una lista por cliente y una pila de historial.

Operaciones CREATE/DELETE se manejan vía eventos RabbitMQ.

🧪 Pruebas
Se puede enviar un ticket usando Postman al endpoint /tickets

Se visualiza la estructura del sistema en consola (toString de árbol, lista y pila)

RabbitMQ muestra los mensajes enviados a las colas turno.create y turno.delete

