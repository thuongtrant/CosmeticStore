workspace "Cosmetic Store - Container Diagram" "Level 2: Container Diagram showing the high-level technology choices and container interactions" {

    !identifiers hierarchical

    model {
        # Primary Actors
        customer = person "Customer" "End users purchasing cosmetic products"
        admin = person "Administrator" "Store administrators managing the platform"
        
        # External Systems
        googleOAuth = softwareSystem "Google OAuth2" "Google authentication service" {
            tags "External System"
        }
        facebookOAuth = softwareSystem "Facebook OAuth2" "Facebook authentication service" {
            tags "External System"
        }
        momoPayment = softwareSystem "MoMo Payment Gateway" "Vietnamese e-wallet payment service" {
            tags "External System"
        }
        firebaseService = softwareSystem "Firebase" "Real-time messaging and configuration service" {
            tags "External System"
        }
        cloudinaryService = softwareSystem "Cloudinary" "Image management and CDN service" {
            tags "External System"
        }
        emailService = softwareSystem "Email Service" "SMTP email delivery service" {
            tags "External System"
        }
        
        # Main Software System with Containers
        cosmeticStore = softwareSystem "BFYStore - Cosmetic Store Platform" "E-commerce platform for beauty products" {
            
            # Frontend Container - Customer SPA
            customerWebApp = container "Customer Web Application" "Single-page application providing shopping experience for customers" {
                technology "React 19.1.1, React Router, Bootstrap 5, Axios, Firebase SDK"
                tags "Web App,Frontend"
                url "http://localhost:3000"
            }
            
            # Frontend Container - Admin Panel (Server-side rendered)
            adminWebApp = container "Admin Web Interface" "Server-side rendered admin panel for store management" {
                technology "Thymeleaf, Bootstrap 5, Spring MVC, jQuery"
                tags "Web App,Admin"
                url "http://localhost:8080/admin"
            }
            
            # Backend Container - Main API
            apiBackend = container "Spring Boot API Backend" "RESTful API handling business logic, authentication, and data processing" {
                technology "Spring Boot 3.5.3, Java 21, Spring Security, JWT, OAuth2, JPA/Hibernate"
                tags "API,Backend"
                url "http://localhost:8080"
            }
            
            # Database Container
            database = container "MySQL Database" "Stores all application data including users, products, orders, and chat messages" {
                technology "MySQL 8.0, HikariCP Connection Pool"
                tags "Database"
                # Removed invalid URL; connection details moved to description/technology
            }
        }

        # User Interactions with Containers
        customer -> cosmeticStore.customerWebApp "Uses web browser to shop, chat, manage orders" "HTTPS"
        admin -> cosmeticStore.adminWebApp "Uses web browser to manage store operations" "HTTPS"
        admin -> cosmeticStore.customerWebApp "May access customer interface for testing" "HTTPS"
        
        # Frontend to Backend Communications
        cosmeticStore.customerWebApp -> cosmeticStore.apiBackend "Makes API calls for authentication, products, orders, chat" "HTTPS/REST + JWT"
        cosmeticStore.adminWebApp -> cosmeticStore.apiBackend "Uses Spring MVC controllers and direct service calls" "HTTP/Internal"
        
        # Backend to Database
        cosmeticStore.apiBackend -> cosmeticStore.database "Reads from and writes to using JPA/Hibernate ORM" "JDBC/MySQL Protocol"
        
        # Backend to External Systems
        cosmeticStore.apiBackend -> googleOAuth "Handles OAuth2 authentication flow" "HTTPS/OAuth2"
        cosmeticStore.apiBackend -> facebookOAuth "Handles OAuth2 authentication flow" "HTTPS/OAuth2"
        cosmeticStore.apiBackend -> momoPayment "Processes payments, handles callbacks" "HTTPS/REST API"
        cosmeticStore.apiBackend -> firebaseService "Manages chat configuration and admin operations" "HTTPS/Firebase Admin SDK"
        cosmeticStore.apiBackend -> cloudinaryService "Uploads and manages product images" "HTTPS/Cloudinary SDK"
        cosmeticStore.apiBackend -> emailService "Sends notifications and password reset emails" "SMTP"
        
        # Frontend Direct to External Services
        cosmeticStore.customerWebApp -> firebaseService "Real-time chat messaging via Firestore" "HTTPS/Firebase Web SDK"
        cosmeticStore.customerWebApp -> googleOAuth "Initiates social login redirect" "HTTPS/OAuth2"
        cosmeticStore.customerWebApp -> facebookOAuth "Initiates social login redirect" "HTTPS/OAuth2"
        
        # Payment Flow (Customer through Payment Gateway)
        customer -> momoPayment "Completes payment in MoMo interface" "HTTPS/Mobile App"
    }

    views {
        container cosmeticStore "CosmeticStore-Containers" "Container diagram for BFYStore showing the high-level technology choices" {
            include *
            title "BFYStore - Container Diagram"
            description "This diagram shows the containers (applications and data stores) that make up the Cosmetic Store platform and how they interact with each other and external systems."
            autolayout lr
        }

        styles {
            element "Element" {
                color #000000
                stroke #cccccc
                strokeWidth 2
                shape roundedbox
                fontSize 11
            }
            element "Person" {
                background #08427b
                color #ffffff
                shape person
                fontSize 12
            }
            element "External System" {
                background #999999
                color #ffffff
                fontSize 10
            }
            element "Web App" {
                background #1168bd
                color #ffffff
                shape webBrowser
                fontSize 11
            }
            element "Frontend" {
                background #1168bd
                color #ffffff
            }
            element "Admin" {
                background #2e6da4
                color #ffffff
            }
            element "API" {
                background #438dd5
                color #ffffff
                shape roundedbox
            }
            element "Backend" {
                background #438dd5
                color #ffffff
            }
            element "Database" {
                background #3ca2c4
                color #ffffff
                shape cylinder
            }
            relationship "Relationship" {
                thickness 2
                color #707070
                fontSize 9
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}