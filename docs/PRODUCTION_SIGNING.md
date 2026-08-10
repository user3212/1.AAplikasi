# Production APK signing

The normal `Build Android Release APK` workflow intentionally remains suitable for CI/testing and may use the generated debug keystore when production secrets are not configured.

For the final distributable APK, use the manual **Production Release APK** workflow. It refuses to build unless all four signing secrets are present.

## 1. Create the production upload keystore locally

Run this on a computer you control. Choose and keep the passwords safe.

```bash
keytool -genkeypair \
  -v \
  -keystore my-upload-key.jks \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Do **not** commit `my-upload-key.jks` to GitHub.

## 2. Convert the keystore to base64

Linux/macOS:

```bash
base64 -w 0 my-upload-key.jks > my-upload-key.jks.base64
```

Windows PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("my-upload-key.jks")) | Set-Content -NoNewline my-upload-key.jks.base64
```

## 3. Add GitHub Actions secrets

Repository: **Settings → Secrets and variables → Actions → New repository secret**

Create exactly these secrets:

- `KEYSTORE_BASE64` — contents of `my-upload-key.jks.base64`
- `STORE_PASSWORD` — keystore password
- `KEY_ALIAS` — normally `upload`
- `KEY_PASSWORD` — key password

Never put any of these values in source files, workflow YAML, issues, or chat messages.

## 4. Run the production workflow

Open **Actions → Production Release APK → Run workflow**.

The workflow restores the keystore only on the runner, builds `assembleRelease`, verifies the APK with `apksigner`, uploads the signed APK as `Guruqu-Production-Release-APK`, and deletes the temporary keystore before the job ends.

## 5. Important

The production signing key is the identity used for future updates. Keep a secure backup. Losing it can prevent updates to an already-distributed APK signed with that key.
