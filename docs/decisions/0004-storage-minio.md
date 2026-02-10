# ADR-0004 – MinIO for Object Storage

## Status

Accepted

## Context

Products may require:

- Images
- Media assets

System must support S3-compatible storage.

## Decision

Use MinIO as object storage.

Access pattern:

Controller → Service → StorageService → MinIO client

Direct MinIO access from controllers is forbidden.

## Rationale

- S3 compatible
- Local docker support
- Easy future migration to AWS S3

## Consequences

- Object references stored in database
- Deletion must be coordinated with entity lifecycle
- Storage operations must be transactional-aware
