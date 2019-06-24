#!/bin/bash

#   Deprecated: Use import2.sh instead.

#   This script imports some submodules as subtrees.

set -o errexit
set -o nounset
set -o pipefail

# -------------------- begin configurable part

#   To get your Eclipse installation's build_id:
#   - Run Eclipse.
#   - Choose menu: Help > About Eclipse SDK.
#   - Note the "Build id".

build_id=I20190605-1800

#   Replace this with the path to your local clone of <https://github.com/eclipse/eclipse.platform.releng.aggregator>.
#   Do not put your work in $repo because it will be discarded by this script.

repo=/junk/eclipse-src/eclipse.platform.releng.aggregator

#   Replace this with the list of child repositories containing the projects you are interested in.

children=(
    eclipse.jdt.ui
    eclipse.jdt.debug
    eclipse.platform.ui
    rt.equinox.framework
)

# -------------------- end configurable part

#   Violently restore $repo to the state used to build your Eclipse installation.

pushd "$repo"
git checkout --force --detach $build_id
git submodule update --recursive --checkout --force --no-fetch
popd

#   Add some submodules as subtrees.

for child in "${children[@]}"; do
    if [ -d "$child" ]; then
        echo "Warning: The directory $child already exists."
        echo "If you have not changed the build_id, you can ignore this message"
        echo "If you have changed the build_id, you must delete that directory first."
        echo "Otherwise, the result will be wrong."
    else
        git subtree add --prefix "$child" "$repo/$child" $build_id
    fi
done
