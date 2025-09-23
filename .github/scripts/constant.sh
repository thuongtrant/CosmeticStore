#!/bin/bash

CONSTANT_DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$CONSTANT_DIR_NAME/../.." && pwd)"

export ROOT_DIR="$ROOT_DIR"
export GITHUB_SCRIPTS_DIR="$ROOT_DIR/.github/scripts"
export GITHUB_SHARED_DIR="$GITHUB_SCRIPTS_DIR/shared"
export GITHUB_ENV_DIR="$ROOT_DIR/.github/env"
export DEBUG_MODE="false"

