#!/usr/bin/env bash
#
# Download PostGIS Dockerfile and related scripts from the official
# docker-postgis repository.
#
# Source: https://github.com/postgis/docker-postgis
#
# Usage:
#   ./download-postgis-dockerfile.sh <pg_version> [postgis_version]
#
# Examples:
#   ./download-postgis-dockerfile.sh 16         # -> 16-3.5
#   ./download-postgis-dockerfile.sh 17         # -> 17-3.5
#   ./download-postgis-dockerfile.sh 17 3.6     # -> 17-3.6
#   ./download-postgis-dockerfile.sh 18         # -> 18-3.6
#

set -euo pipefail

# ---------------------------------------------------------------------------
# Resolve default PostGIS version for each supported PostgreSQL version
# ---------------------------------------------------------------------------
resolve_postgis_version() {
  local pg_ver="$1"
  case "${pg_ver}" in
    16) echo "3.5" ;;
    17) echo "3.5" ;;
    18) echo "3.6" ;;
    *)
      echo "Error: unsupported PostgreSQL version '${pg_ver}'." >&2
      echo "Supported versions: 16, 17, 18." >&2
      exit 1
      ;;
  esac
}

# ---------------------------------------------------------------------------
# Validate that the pg-postgis combination exists in the repository
# ---------------------------------------------------------------------------
VALID_COMBINATIONS=("16-3.5" "17-3.5" "17-3.6" "18-3.6")

validate_combination() {
  local combo="$1"
  for valid in "${VALID_COMBINATIONS[@]}"; do
    if [[ "${combo}" == "${valid}" ]]; then
      return 0
    fi
  done
  echo "Error: combination '${combo}' is not available." >&2
  echo "Valid combinations: ${VALID_COMBINATIONS[*]}" >&2
  exit 1
}

# ---------------------------------------------------------------------------
# Parse arguments
# ---------------------------------------------------------------------------
if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <pg_version> [postgis_version]" >&2
  echo "" >&2
  echo "  pg_version       PostgreSQL major version (16, 17, 18)" >&2
  echo "  postgis_version  PostGIS version (optional, e.g. 3.5, 3.6)" >&2
  echo "" >&2
  echo "Valid combinations: ${VALID_COMBINATIONS[*]}" >&2
  exit 1
fi

PG_VERSION="$1"
POSTGIS_VERSION="${2:-$(resolve_postgis_version "${PG_VERSION}")}"
TAG="${PG_VERSION}-${POSTGIS_VERSION}"

validate_combination "${TAG}"

# ---------------------------------------------------------------------------
# Download files
# ---------------------------------------------------------------------------
REPO_BASE_URL="https://raw.githubusercontent.com/postgis/docker-postgis/master/${TAG}"
DEST_DIR="docker/postgis"

FILES=(
  "Dockerfile"
  "initdb-postgis.sh"
  "update-postgis.sh"
)

echo "==> PostgreSQL ${PG_VERSION} + PostGIS ${POSTGIS_VERSION} (${TAG})"
echo "==> Creating destination directory: ${DEST_DIR}"
mkdir -p "${DEST_DIR}"

for file in "${FILES[@]}"; do
  url="${REPO_BASE_URL}/${file}"
  dest="${DEST_DIR}/${file}"
  echo "==> Downloading ${file} ..."
  curl -fsSL -o "${dest}" "${url}"
  echo "    Saved to ${dest}"
done

# Make shell scripts executable
chmod +x "${DEST_DIR}"/*.sh

echo ""
echo "Done! Files for ${TAG} downloaded into ${DEST_DIR}/"
ls -la "${DEST_DIR}/"
