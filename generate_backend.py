import os

services = {
    "api-gateway": {
        "deps": ["org.springframework.cloud:spring-cloud-starter-gateway", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.gateway"
    },
    "auth-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-data-jpa", "org.postgresql:postgresql", "org.springframework.boot:spring-boot-starter-security", "org.springframework.boot:spring-boot-starter-validation", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.auth"
    },
    "catalog-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-data-jpa", "org.postgresql:postgresql", "org.springframework.boot:spring-boot-starter-validation", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.catalog"
    },
    "inventory-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-data-jpa", "org.postgresql:postgresql", "org.springframework.boot:spring-boot-starter-validation", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.inventory"
    },
    "order-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-data-jpa", "org.postgresql:postgresql", "org.springframework.boot:spring-boot-starter-validation", "org.springframework.boot:spring-boot-starter-actuator", "org.springframework.kafka:spring-kafka"],
        "package": "com.smartmobilehub.order"
    },
    "payment-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-data-jpa", "org.postgresql:postgresql", "org.springframework.boot:spring-boot-starter-validation", "org.springframework.boot:spring-boot-starter-actuator", "org.springframework.kafka:spring-kafka"],
        "package": "com.smartmobilehub.payment"
    },
    "notification-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-mail", "org.springframework.kafka:spring-kafka", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.notification"
    },
    "ai-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.ai"
    },
    "analytics-service": {
        "deps": ["org.springframework.boot:spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-data-jpa", "org.postgresql:postgresql", "org.springframework.kafka:spring-kafka", "org.springframework.boot:spring-boot-starter-actuator"],
        "package": "com.smartmobilehub.analytics"
    }
}

parent_pom = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.smartmobilehub</groupId>
    <artifactId>smart-mobile-hub-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2024.0.0</spring-cloud.version>
    </properties>
    <modules>
{modules}
    </modules>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
"""

module_pom = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.smartmobilehub</groupId>
        <artifactId>smart-mobile-hub-backend</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <artifactId>{service_name}</artifactId>
    <name>{service_name}</name>
    <description>{service_name} for Smart Mobile Hub</description>
    <dependencies>
{dependencies}
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
"""

app_java = """package {package};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class {class_name} {{
    public static void main(String[] args) {{
        SpringApplication.run({class_name}.class, args);
    }}
}}
"""

os.makedirs("backend", exist_ok=True)
with open("backend/pom.xml", "w") as f:
    modules_str = "\n".join([f"        <module>{name}</module>" for name in services.keys()])
    f.write(parent_pom.replace("{modules}", modules_str))

for name, info in services.items():
    os.makedirs(f"backend/{name}", exist_ok=True)
    deps_str = ""
    for dep in info["deps"]:
        parts = dep.split(":")
        deps_str += f"        <dependency>\n            <groupId>{parts[0]}</groupId>\n            <artifactId>{parts[1]}</artifactId>\n        </dependency>\n"
    
    with open(f"backend/{name}/pom.xml", "w") as f:
        f.write(module_pom.replace("{service_name}", name).replace("{dependencies}", deps_str))
    
    package_path = info["package"].replace(".", "/")
    os.makedirs(f"backend/{name}/src/main/java/{package_path}", exist_ok=True)
    os.makedirs(f"backend/{name}/src/main/resources", exist_ok=True)
    os.makedirs(f"backend/{name}/src/test/java/{package_path}", exist_ok=True)
    
    with open(f"backend/{name}/src/main/resources/application.yml", "w") as f:
        f.write(f"spring:\n  application:\n    name: {name}\n")
        if "gateway" not in name:
            f.write(f"server:\n  port: 8080\n")
        else:
            f.write(f"server:\n  port: 8080\n") # Gateway can be 8080, others we can set later
            
    class_name = "".join([word.capitalize() for word in name.split("-")]) + "Application"
    with open(f"backend/{name}/src/main/java/{package_path}/{class_name}.java", "w") as f:
        f.write(app_java.replace("{package}", info["package"]).replace("{class_name}", class_name))

print("Backend microservices generated successfully.")
