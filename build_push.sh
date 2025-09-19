mvn clean package
podman build -f ./Containerfile.jvm -t quay.io/jstakun/arrow-ai-assistant
podman push --authfile ~/jstakun-aiassistant-auth.json quay.io/jstakun/arrow-ai-assistant:latest

