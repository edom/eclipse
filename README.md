# A copy of some Eclipse repositories

## Disclaimer

This is not the official repository.
This is my copy for maintaining my patches.

The history of this repository will be periodically rewritten.

To see the Git commits in this repository:

```
git log --reverse --first-parent --oneline --decorate=short
```

For the list of my customizations,
see [com.spacetimecat.eclipse.feature/README.md](com.spacetimecat.eclipse.feature/README.md).

## Directories

- `workspace` is the workspace I use to develop my custom Eclipse.
- `workspace2` is the workspace that I run my custom Eclipse on.

## Running

I tried to create a p2 update site, but I ran out of patience,
so we will have to do with running two Eclipse instances.

The software versions in the `base` branch are:

```
Eclipse SDK I20190605-1800
Mylyn 3.24.2
```

(The `base` branch is described in [import2.sh](import2.sh).)

1. Install the Eclipse SDK whose version matches the version I am using above.
1. Clone this Git repository and check out the `master` branch.
1. Run Eclipse.
1. File > Import > Team > Team Project > (import `STC-Eclipse.psf`).
1. File > Import > Run/Debug > Launch Configurations > (import `STC-Eclipse.launch`).
1. Debug or Run the `STC-Eclipse` configuration.

## More information

More information is in my Eclipse notes:

https://edom.github.io/eclipse.html
