# kandy-cpaas-sample-android-kotlin
It is the sample app of CPaaS modules (Call, SMS, Chat)

### Install as Android App
You can download the .apk file from [releases](https://github.com/hclsampleapps/kandy-cpaas-sample-android-kotlin/releases) section.
If you are a developer, then you can generate the apk from source code.

Note: Minimum version required for install apk is kitkat (4.4)

### Run this Android App
1. Open Android App
2. Enter the credentials of a user.

### Execute commands for development
1. Setup repository via `https://github.com/hclsampleapps/kandy-cpaas-sample-android-kotlin.git`
2. Resolve build dependency via sync gradle in android studio
3. Generate final build by android studio

### Branching strategy
We are following **GitFlow** as the branching strategy and for release management.

The central repo holds two main branches with an infinite lifetime:

- main
- develop

The `main` branch at origin should be familiar to every Git user. Parallel to the `main` branch, another branch exists called `develop`.

We consider `origin/main` to be the main branch where the source code of HEAD always reflects a *production-ready* state.

We consider `origin/develop` to be the main branch where the source code of HEAD always reflects a state with the latest delivered development changes for the next release.

#### Supporting branches
Next to the main branches `main` and `develop`, our development model uses a variety of supporting branches to aid parallel development between team members.

The different types of branches we may use are:

- Feature branches
- Release branches
- Hotfix branches

To know more about *GitFlow*, please refer

- [Introducing GitFlow](https://datasift.github.io/gitflow/IntroducingGitFlow.html)
- [A successful Git branching model](https://nvie.com/posts/a-successful-git-branching-model/)

### Coding conventions

Contributors should strictly follow JAVA standard conventions:

1. Class names are in camelCase
2. Package names start with lowerCase
3. Variable names are in camelCase
