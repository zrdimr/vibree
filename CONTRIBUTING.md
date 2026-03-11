# Contributing to Vibree

Welcome! We love your input and want to make contributing to this repository as easy and transparent as possible.

Whether you're helping us optimize our LibTorch mobile pipelines, refining our Kotlin Coroutine architecture, or suggesting UI/UX improvements, your help is appreciated.

## Contribution Workflow

We generally follow a "fork-and-pull" Git workflow.

### 1. Branching
1. Fork the repo into your GitHub account.
2. Clone your fork locally using `git clone https://github.com/your-username/vibree.git`.
3. Choose the appropriate project path (`cd ios/Vibree` or `cd android`).
4. Create a new branch reflecting the feature you're adding (e.g. `feat/improve-eda-sampling` or `fix/nlp-crashes`).

### 2. Development & Standards
- Ensure you adhere to **SwiftLint** on iOS and **Ktlint** on Android.
- When committing, try to use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) formats, e.g. `feat(android): add heart rate streaming abstraction`.
- **Testing**: While unit testing might be difficult on hardware-coupled features, any new modules specifically dealing with `.ptl` (Model Inference) or data structures must be accompanied by relevant test assertions.

### 3. Integrating AI Models
If your PR touches the NLP or HRV processing logic, you **must not** commit the actual `.ptl` weight sizes. PyTorch Mobile `.ptl` files exceed GitHub repository limits and should be loaded dynamically using the automated Google Drive script defined in our `.github/workflows/`. Instead, simply refer to their specific layer architectures in your Pull Request description.

### 4. Creating a Pull Request
1. Commit your changes and push them to your fork.
2. Open a Pull Request from your branch into the `main` branch of the upstream Vibree repository.
3. Your code will undergo the mandatory GitHub Actions checks for both iOS and Android. Ensure these checks pass (green) before requesting a review.

## Reporting Issues 🐛

Use GitHub Issues to report bugs or request features. When filing a bug:
- Provide clear steps to reproduce the crash.
- Include log outputs (e.g., Xcode debug symbols or LogCat traces).
- Detail your local development environment (OS version, Xcode/Android Studio version, Physical Device vs Simulator).

Thank you for contributing to the future of Edge-AI physiological health!
