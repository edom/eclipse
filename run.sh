#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

eclipse=/junk/eclipse/eclipse

program=$1

shift

case "$program" in
    (eclipse)
        "$eclipse" "$@"
        ;;
    (p2-director)
        "$eclipse" -noSplash -application org.eclipse.equinox.p2.director "$@"
        ;;

    # https://help.eclipse.org/2019-06/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fp2_mirror.html
    (p2-mirror-artifact)
        "$eclipse" -noSplash -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication "$@"
        ;;
    (p2-mirror-metadata)
        "$eclipse" -noSplash -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication "$@"
        ;;

    (*)
        echo "Unknown program: $1"
        ;;
esac
