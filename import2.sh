#!/bin/bash

#   Do not run this script.
#   Instead, copy some parts.

git clone git@github.com:eclipse/mylyn.tasks.git /junk/eclipse-src/org.eclipse.mylyn.tasks

pushd /junk/eclipse-src/eclipse.platform.swt.binaries/
git checkout -f I20190605-1800
popd
rsync -av --delete --exclude=/.git/ /junk/eclipse-src/eclipse.platform.swt.binaries/ eclipse.platform.swt.binaries
rsync -av --delete --exclude=/.git/ /junk/eclipse-src/eclipse.platform.runtime/ eclipse.platform.runtime

git subtree add --prefix eclipse.platform /junk/eclipse-src/eclipse.platform.releng.aggregator/eclipse.platform I20190605-1800
git subtree add --prefix rt.equinox.p2 /junk/eclipse-src/eclipse.platform.releng.aggregator/rt.equinox.p2 I20190605-1800
git subtree add --prefix eclipse.pde.ui /junk/eclipse-src/eclipse.platform.releng.aggregator/eclipse.pde.ui I20190605-1800
git subtree add --prefix eclipse.platform.swt /junk/eclipse-src/eclipse.platform.releng.aggregator/eclipse.platform.swt I20190605-1800
git subtree add --prefix eclipse.platform.text /junk/eclipse-src/eclipse.platform.releng.aggregator/eclipse.platform.text I20190605-1800
git subtree add --prefix org.eclipse.mylyn.tasks /junk/eclipse-src/org.eclipse.mylyn.tasks R_3_24_2
