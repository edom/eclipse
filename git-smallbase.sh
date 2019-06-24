#!/bin/bash

# Create a small base for fast rebase.

set -o errexit
set -o nounset
set -o pipefail

paths=()
read_index=my_index_0
write_index=my_index_1

readarray -t paths <<<$(git diff --name-only base master)

rm -f "$read_index"
rm -f "$write_index"

GIT_INDEX_FILE=$read_index git read-tree base
GIT_INDEX_FILE=$read_index git ls-files --stage "${paths[@]}" | GIT_INDEX_FILE=$write_index git update-index --index-info

tree=$(GIT_INDEX_FILE=$write_index git write-tree)
commit=$(GIT_INDEX_FILE=$write_index git commit-tree -m "smallbase (${#paths[@]} files)" "$tree")

git branch -f smallbase "$commit"

rm -f "$read_index"
rm -f "$write_index"
