#!/bin/bash

gcloud run deploy triptide-backend-staging \
  --source . \
  --region asia-east2 \
  --allow-unauthenticated \
  --env-vars-file env.staging.yaml