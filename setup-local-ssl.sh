#!/bin/bash

set -euo pipefail

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
ORANGE='\033[0;33m'
NC='\033[0m' # No Color

# ========= CONFIGURATION =========
DOMAINS=("cosmetic.dev" "api.cosmetic.dev")
IPV4="127.0.0.1"
CERT_DIR="./certs"
CERT_FILE="$CERT_DIR/local-cert.pem"
KEY_FILE="$CERT_DIR/local-key.pem"
NGINX_CONF="./nginx.conf"
# ============================

usage() {
	cat <<EOF
Usage:
  $0 init            # (recommended) create cert + add hosts + status
  $0 certs           # only create mkcert certificates
  $0 hosts add       # add entries to /etc/hosts
  $0 hosts remove    # remove entries from /etc/hosts
  $0 hosts status    # check if host entries exist
EOF
}

require_cmd() { command -v "$1" >/dev/null 2>&1 || {
	echo "❌ Missing command: $1"
	echo "→ Install mkcert: brew install mkcert nss" # (nss for Firefox)
	exit 1
}; }

hosts_file() { echo "/etc/hosts"; }

has_line() {
	local line="$1"
	grep -Fq "$line" "$(hosts_file)"
}

flush_dns() {
	if [[ "$(uname -s)" == "Darwin" ]]; then
		echo -e "${YELLOW}•${NC} Flush DNS (macOS)…"
		sudo dscacheutil -flushcache || true
		sudo killall -HUP mDNSResponder || true
	else
		echo -e "${YELLOW}•${NC} Flush DNS (Linux) — usually not needed."
	fi
}

add_hosts() {
	local changed=0
	echo ""
	echo -e "✅ Hosts file:"
	for d in "${DOMAINS[@]}"; do
		local line="${IPV4} ${d}"
		if has_line "$line"; then
			echo -e "${YELLOW}•${NC} Already exists: $line"
		else
			echo -e "${YELLOW}•${NC} Adding: $line"
			echo "$line" | sudo tee -a "$(hosts_file)" >/dev/null
			changed=1
		fi
	done
	[[ $changed -eq 1 ]] && flush_dns
}

remove_hosts() {
	local tmp
	tmp="$(mktemp)"
	sudo cp "$(hosts_file)" "$tmp"
	for d in "${DOMAINS[@]}"; do
		sudo sed -i.bak "/^[#]*\s*${IPV4}[[:space:]]\+${d}\s*$/d" "$tmp" || true
	done
	if ! diff -q "$tmp" "$(hosts_file)" >/dev/null; then
		echo -e "${YELLOW}•${NC} Updating $(hosts_file)"
		sudo cp "$tmp" "$(hosts_file)"
		flush_dns
	else
		echo -e "${YELLOW}•${NC} Nothing to remove."
	fi
	rm -f "$tmp"
}

status_hosts() {
	local ok=1
	for d in "${DOMAINS[@]}"; do
		local line="${IPV4} ${d}"
		if has_line "$line"; then
			echo "✅ Found: $line"
		else
			echo "❌ Missing: $line"
			ok=0
		fi
	done
	return $((ok ? 0 : 1))
}

make_certs() {
	require_cmd mkcert
	mkdir -p "$CERT_DIR"
	echo -e "${YELLOW}•${NC} Installing local CA (if not already installed)…"
	mkcert -install
	echo -e "${YELLOW}•${NC} Creating certificate for: ${DOMAINS[*]}"
	mkcert -cert-file "$CERT_FILE" -key-file "$KEY_FILE" "${DOMAINS[@]}"
	echo -e "✅ Certs created:"
	ls -l "$CERT_FILE" "$KEY_FILE"
	echo
	echo "✅ Map to nginx.conf:"
	echo "  ssl_certificate     /etc/nginx/certs/$(basename "$CERT_FILE");"
	echo "  ssl_certificate_key /etc/nginx/certs/$(basename "$KEY_FILE");"
	echo ""
	echo "✅ Map to nginx service:"
	echo "  nginx:"
	echo "    ports:"
	echo "      - \"443:443\""
	echo "    volumes:"
	echo "      - ${NGINX_CONF}:/etc/nginx/nginx.conf:ro"
	echo "      - ${CERT_DIR}:/etc/nginx/certs:ro"
}

init_all() {
	make_certs
	add_hosts
	status_hosts
}

main() {
	case "${1:-}" in
		init) init_all ;;
		certs) make_certs ;;
		hosts)
			case "${2:-}" in
			add) add_hosts ;;
			remove) remove_hosts ;;
			status) status_hosts ;;
			*)
				usage
				exit 2
				;;
			esac
			;;
		*)
			usage
			exit 2
			;;
	esac
}

main "$@"
