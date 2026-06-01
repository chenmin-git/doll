#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:3000/api}"

need() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "Missing required command: $1" >&2
    exit 1
  }
}

need curl
need jq

post_json() {
  curl -fsS -H 'Content-Type: application/json' -d "$2" "$BASE_URL$1"
}

get_json() {
  curl -fsS "$BASE_URL$1"
}

assert_code() {
  local label="$1"
  local json="$2"
  local code
  code="$(jq -r '.code' <<<"$json")"
  if [[ "$code" != "200" ]]; then
    echo "FAIL $label: expected code 200, got $code" >&2
    jq . <<<"$json" >&2
    exit 1
  fi
  echo "PASS $label"
}

buyer_login="$(post_json /user/login '{"username":"buyer1","password":"123456"}')"
seller_login="$(post_json /user/login '{"username":"seller1","password":"123456"}')"
admin_login="$(post_json /user/login '{"username":"admin","password":"admin123"}')"

assert_code "buyer login" "$buyer_login"
assert_code "seller login" "$seller_login"
assert_code "admin login" "$admin_login"

[[ "$(jq -r '.data.role' <<<"$buyer_login")" == "1" ]]
[[ "$(jq -r '.data.role' <<<"$seller_login")" == "2" ]]
[[ "$(jq -r '.data.role' <<<"$admin_login")" == "3" ]]

assert_code "product search" "$(curl -fsS --get --data-urlencode 'keyword=娃' "$BASE_URL/product/search")"
assert_code "buyer cart" "$(get_json /cart/user/4)"
assert_code "buyer orders" "$(get_json /order/buyer/4)"
assert_code "seller products" "$(get_json /product/seller/7)"
assert_code "seller orders" "$(get_json /order/seller/7)"
assert_code "admin users" "$(get_json /user/list)"
assert_code "complaints" "$(get_json /complaint/list)"
assert_code "news" "$(get_json /news/list)"
assert_code "posts" "$(get_json /post/list)"
assert_code "auctions" "$(get_json /auction/list)"

echo "Smoke API checks completed against $BASE_URL"
