#!/bin/sh

echo "waiting for localstack to be ready"

until aws --endpoint-url=http://localstack:4566 sqs list-queues; do
    >&2 echo "SQS is not ready.. sleeping"
      sleep 1
done

echo "Localstack is ready"

echo "Creating SQS"

aws sqs create-queue --endpoint-url=http://localstack:4566 --queue-name commerce_events.fifo --attributes "FifoQueue=true,ContentBasedDeduplication=true"

echo "SQS has been created"