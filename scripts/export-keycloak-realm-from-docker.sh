#!/usr/bin/env bash
#
# Export the Keycloak realm configuration from the running Docker container
# and save it to the project root as keycloak-realm.json.
#
# The script connects to the "bibs-keycloak" container, runs the Keycloak
# export command for the "bibs" realm, and copies the resulting JSON file
# back to the host.
#
# Usage:
#   ./scripts/export-keycloak-realm-from-docker.sh
#
# Prerequisites:
#   - Docker must be installed and running.
#   - The "bibs-keycloak" container must be up (docker compose up).
#

set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------
CONTAINER_NAME="bibs-keycloak"
REALM_NAME="bibs"
CONTAINER_EXPORT_PATH="/tmp/keycloak-realm-export.json"
LOCAL_EXPORT_PATH="keycloak-realm.json"

# ---------------------------------------------------------------------------
# Resolve project root (parent of scripts/)
# ---------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# ---------------------------------------------------------------------------
# Preflight checks
# ---------------------------------------------------------------------------
if ! command -v docker &>/dev/null; then
  echo "Error: docker is not installed or not in PATH." >&2
  exit 1
fi

if ! docker inspect "${CONTAINER_NAME}" &>/dev/null; then
  echo "Error: container '${CONTAINER_NAME}' is not running." >&2
  echo "Start it with: docker compose up -d" >&2
  exit 1
fi

# ---------------------------------------------------------------------------
# Export realm
# ---------------------------------------------------------------------------
echo "==> Removing previous export file from container (if any)..."
docker exec -u root "${CONTAINER_NAME}" \
  sh -c "rm -f ${CONTAINER_EXPORT_PATH}"

echo "==> Exporting realm '${REALM_NAME}' from container '${CONTAINER_NAME}'..."
docker exec -u root "${CONTAINER_NAME}" \
  /opt/keycloak/bin/kc.sh export \
    --file="${CONTAINER_EXPORT_PATH}" \
    --realm="${REALM_NAME}" \
    --optimized

echo "==> Copying export file to ${LOCAL_EXPORT_PATH}..."
docker cp "${CONTAINER_NAME}:${CONTAINER_EXPORT_PATH}" "${PROJECT_ROOT}/${LOCAL_EXPORT_PATH}"

echo ""
echo "Done! Realm '${REALM_NAME}' exported to ${PROJECT_ROOT}/${LOCAL_EXPORT_PATH}"
