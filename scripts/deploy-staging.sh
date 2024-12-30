#!/bin/bash

# Configure Docker authentication
gcloud auth configure-docker asia-east2-docker.pkg.dev

# Build the Docker image
docker build -t asia-east2-docker.pkg.dev/triptide-446119/triptide/backend:latest .

# Push to Container Registry
docker push asia-east2-docker.pkg.dev/triptide-446119/triptide/backend:latest

# Deploy to Cloud Run staging environment
gcloud run deploy triptide-backend-staging \
  --image asia-east2-docker.pkg.dev/triptide-446119/triptide/backend:latest \
  --region asia-east2 \
  --platform managed \
  --allow-unauthenticated \
  --env-vars-file env.staging.yaml