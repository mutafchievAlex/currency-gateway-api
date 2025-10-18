# syntax=docker/dockerfile:1
FROM rabbitmq:3.12-management

# Expose ports for AMQP and management UI
EXPOSE 5672 15672

# Default credentials can be overridden at runtime
ENV RABBITMQ_DEFAULT_USER=gateway
ENV RABBITMQ_DEFAULT_PASS=gateway
ENV RABBITMQ_DEFAULT_VHOST=/

# Load the bundled definitions so the statistics exchange exists on boot.
COPY definitions.json /etc/rabbitmq/definitions.json

# Configure RabbitMQ to load the provided definitions on boot.
RUN printf '\nload_definitions = /etc/rabbitmq/definitions.json\n' >> /etc/rabbitmq/rabbitmq.conf
