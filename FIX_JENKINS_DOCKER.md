# 🔧 Fix Jenkins Docker Permission Issue

The error `permission denied while trying to connect to the Docker daemon socket` happens because the Jenkins container doesn't have permission to use the host's Docker engine.

Here are two ways to fix this.

## Option 1: The Quick Fix (Recommended)
Run this command in your **Windows PowerShell** or Command Prompt to instantly fix the permission for the running container.

1. Find your Jenkins container ID:
   ```bash
   docker ps
   ```
   *Look for the container image `jenkins/jenkins`.*

2. Run this command to grant permission (replace `<container_id>` with your actual ID):
   ```bash
   docker exec -u 0 -it <container_id> chmod 666 /var/run/docker.sock
   ```

3. **Re-run the Jenkins build** immediately. It should pass.

---

## Option 2: The Permanent Fix (Restart Container)
If you restart the container, you might lose data if you haven't mapped volumes correctly. Only do this if you know your `jenkins_home` is persisted.

Stop and remove the current container, then run:

```bash
docker run -d ^
  --name jenkins ^
  -p 8080:8080 ^
  -p 50000:50000 ^
  -u root ^
  -v //var/run/docker.sock:/var/run/docker.sock ^
  -v jenkins_home:/var/jenkins_home ^
  jenkins/jenkins:lts
```
*Note: The `-u root` flag runs Jenkins as root, avoiding permission issues.*
