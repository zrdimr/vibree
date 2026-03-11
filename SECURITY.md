# Security Policy

At Vibree, we take the security of our wearable application, deep-learning models, and user data extremely seriously. We are committed to protecting the privacy of the personal and physiological data collected by this repository.

## Supported Versions

We only provide security updates and patches for the current major release version.

| Version | Supported          |
| ------- | ------------------ |
| v1.0.x  | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

If you discover any security vulnerabilities in this project, particularly weaknesses regarding:
- Improper Edge AI data isolation boundaries.
- Wearable API extraction endpoints.
- Unencrypted CoreData or Room database handling.

Please do **NOT** report it by creating a public GitHub issue. Instead, send an email privately to the project maintainer. 

We will acknowledge your email within 48 hours, and you will receive a more detailed response outlining the next steps in reproducing, triaging, and mitigating the vulnerability. Once the issue is resolved, we will publish a patch alongside a security advisory if community users need to take action.

**Important Data Handling Principles:**
Vibree is fundamentally an **Edge AI** platform. Under absolutely no circumstance should raw Natural Language Processing inputs ("Tempat Curhat" journals) or biometrics be transmitted off-device to unverified cloud hosts. The `MobileBERT` implementation is designed to keep all text inference securely offline. Any PR breaking this air-gap architecture will be rejected.
