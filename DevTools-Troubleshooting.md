# Spring Boot DevTools Troubleshooting Guide

## Current Configuration ✓
Your `pom.xml` already has DevTools correctly configured:
- ✓ Dependency included with `runtime` scope
- ✓ Marked as `optional`
- ✓ Spring Boot Maven plugin present

## Common Issues and Solutions

### 1. **IDE Auto-Build Not Enabled** (Most Common)
**For IntelliJ IDEA:**
- Go to: `File > Settings > Build, Execution, Deployment > Compiler`
- Enable: "Build project automatically"
- Go to: `File > Settings > Advanced Settings`
- Enable: "Allow auto-make to start even if developed application is currently running"

**For Eclipse:**
- Go to: `Project > Build Automatically`

**For VS Code:**
- This requires a language server setup

### 2. **Running Application in Production Mode**
- Make sure you're running in **development** mode (not production)
- DevTools is automatically disabled in packaged/JAR deployments
- When running from IDE, it should work

### 3. **Trigger File Changes**
DevTools watches for changes in:
- Java source files (`.java`)
- Property files (`application.properties`, `application.yml`)
- Templates (`.html`, `.xml`)
- Static resources

**NOT watched by default:**
- POM.xml changes (requires restart)
- External library changes (requires restart)

### 4. **Add Spring Boot Maven Plugin Configuration**
Add the `fork` configuration for better hot reload:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <fork>true</fork>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>
```

### 5. **Disable DevTools Cache**
Add to `application.properties`:
```properties
# Disable DevTools cache
spring.devtools.restart.enabled=true
spring.devtools.restart.poll-interval=2000ms
spring.devtools.restart.quiet-period=1000ms
spring.devtools.livereload.enabled=true
```

### 6. **Debug DevTools Status**
Add to `application.properties`:
```properties
logging.level.org.springframework.boot.devtools=DEBUG
```

## Quick Checklist
- [ ] IDE auto-build is enabled
- [ ] Running application from IDE (not as JAR)
- [ ] Modified `.java` or property files
- [ ] Waited a moment for build to complete
- [ ] Check console for "Started" message
- [ ] DevTools dependency scope is `runtime`
- [ ] Spring Boot version is 2.3.0+

## Running from Command Line (if needed)
```bash
mvn spring-boot:run
```

This should enable DevTools hot reload properly.

