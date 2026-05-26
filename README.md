# Push Docker Image to Amazon ECR

A Spring Boot REST API containerized with a multi-stage Docker build and automatically deployed to Amazon ECR via GitHub Actions using OIDC-based authentication — no static AWS credentials stored anywhere.

---

## Table of Contents

- [Application](#application)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
  - [1. Containerization](#1-containerization)
  - [2. Secure AWS Authentication via OIDC](#2-secure-aws-authentication-via-oidc)
  - [3. CI/CD Pipeline](#3-cicd-pipeline)
  - [4. Infrastructure as Code](#4-infrastructure-as-code)
- [Security Highlights](#security-highlights)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)

---

## Application

A minimal Spring Boot REST API that returns a random quote.

**Endpoint**

```
GET /quotes/random
```

**Sample Response**

```json
{
  "id": 1,
  "author": "Albert Einstein",
  "quote": "Imagination is more important than knowledge."
}
```

---

## Project Structure

```
.
├── src/                            # Spring Boot application source code
├── pom.xml                         # Maven build descriptor
├── Dockerfile                      # Multi-stage Docker build
├── .dockerignore                   # Files excluded from the Docker build context
├── iac/
│   └── ecr.yml                     # CloudFormation template for the ECR repository
├── .github/
│   └── workflows/
│       └── deploy.yml              # GitHub Actions CI/CD pipeline
└── README.md
```

---

## How It Works

### 1. Containerization

The `Dockerfile` uses a two-stage build to produce a lean, secure image:

| Stage | Base Image | Purpose |
| --- | --- | --- |
| **Build** | `maven:3.9-eclipse-temurin-21-alpine` | Compiles the source and packages a fat JAR |
| **Runtime** | `eclipse-temurin:21-jre-alpine` | Runs the JAR — no JDK or build tooling included |

The final image runs as a dedicated non-root user (`appuser`) to reduce the container's attack surface.

---

### 2. Secure AWS Authentication via OIDC

No AWS access keys or secrets are stored in GitHub. Authentication works entirely through short-lived tokens:

1. GitHub Actions generates an OIDC token at runtime.
2. The token is exchanged with AWS STS via `AssumeRoleWithWebIdentity`.
3. AWS validates the token against the configured OIDC identity provider.
4. AWS issues temporary credentials scoped to the IAM role.
5. The IAM role's trust policy restricts access to the `peprah12git/push-Docker-image` repository only.

---

### 3. CI/CD Pipeline

The workflow (`.github/workflows/deploy.yml`) triggers on every push to any branch and runs the following steps in order:

1. Check out the repository code.
2. Authenticate to AWS using the OIDC token — no static credentials.
3. Log in to Amazon ECR.
4. Build the Docker image using the multi-stage `Dockerfile`.
5. Tag the image as `peprah12git_quote-api`.
6. Push the image to the private ECR repository (`peprah-docker-image-push`).

---

### 4. Infrastructure as Code

The ECR repository is provisioned by `iac/ecr.yml` (AWS CloudFormation). The template enforces the following:

| Feature | Configuration |
| --- | --- |
| Image scanning | Enabled on every push |
| Tag immutability | Enabled — existing tags cannot be overwritten |
| Lifecycle policy | Untagged images expire after 7 days; only the last 5 tagged images are retained |
| Repository policy | Push access restricted to the GitHub Actions IAM role only |
| Encryption | AES-256 at rest |
| Transport | HTTP access denied — HTTPS only |
| Resource tags | `Project`, `Owner`, `Environment`, `ManagedBy` |

**Deploy the stack**

```bash
aws cloudformation deploy \
  --template-file iac/ecr.yml \
  --stack-name peprah-ecr-stack \
  --region eu-central-1
```

---

## Security Highlights

- No long-lived AWS credentials stored in GitHub or the codebase.
- OIDC token is short-lived and auto-rotated on every workflow run.
- IAM role trust policy is scoped to a single GitHub repository.
- IAM permissions are scoped to a single ECR repository.
- Container runs as a non-root user.
- ECR enforces HTTPS-only access via repository policy.
- Image scanning detects vulnerabilities on every push.

---

## Prerequisites

| Requirement | Details |
| --- | --- |
| AWS account | With permission to create IAM roles, ECR repositories, and CloudFormation stacks |
| GitHub repository secret | `AWS_ROLE_ARN` — the ARN of the IAM role to assume |
| AWS OIDC identity provider | Configured in IAM for `token.actions.githubusercontent.com` |
| Docker | For local builds (optional) |

---

## Getting Started

1. **Provision the ECR repository** using the CloudFormation template (see [Deploy the stack](#4-infrastructure-as-code) above).

2. **Configure the GitHub secret** — add `AWS_ROLE_ARN` to your repository secrets (`Settings → Secrets and variables → Actions`).

3. **Push to any branch** — the GitHub Actions workflow triggers automatically, builds the image, and pushes it to ECR.

4. **Verify the image** in the AWS console under `ECR → Repositories → peprah-docker-image-push`.