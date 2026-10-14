#!/usr/bin/env bash
# Builds the Docker image and runs the evaluation on examples/.
# Additional arguments restrict the run to the given examples, e.g. ./run.sh Calc Xml
set -eu
cd "$(dirname "$0")"

IMAGE=atg-miner-replication

docker build -t "$IMAGE" .

EXAMPLES_DIR="$(pwd)/examples"
USER_OPT=()
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) EXAMPLES_DIR="$(pwd -W)/examples"; export MSYS_NO_PATHCONV=1 ;;
    *) USER_OPT=(--user "$(id -u):$(id -g)") ;;
esac

docker run --rm "${USER_OPT[@]}" -v "$EXAMPLES_DIR:/work/examples" "$IMAGE" examples/ "$@"

echo
for result in examples/*/result.txt; do
    printf '%-16s %s\n' "$(basename "$(dirname "$result")")" "$(head -n 1 "$result")"
done
