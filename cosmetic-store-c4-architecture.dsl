workspace "Cosmetic Store System" "E-commerce platform for cosmetic products with dual frontend architecture" {

    !identifiers hierarchical

    model {
        # External Actors
        customer = person "Customer" "End users who browse and purchase cosmetic products online"
        admin = person "Admin" "Store administrators who manage products, orders and customer support"
        
        # External Systems
        momoPayment = softwareSystem "MoMo Payment Gateway" "Third-party payment service for processing transactions" {
            tags "External System"
        }
        firebaseService = softwareSystem "Firebase" "Real-time database and authentication service" {
            tags "External System"  
        }
        cloudinaryService = softwareSystem "Cloudinary" "Cloud-based image management and CDN service" {
            tags "External System"
        }
        googleOAuth = softwareSystem "Google OAuth2" "Social login authentication provider" {
            tags "External System"
        }
        
        # Main Software System
        cosmeticStore = softwareSystem "Cosmetic Store Platform" "Complete e-commerce solution for beauty products" {
            
            # Frontend Containers
            customerWeb = container "Customer Web App" "React.js SPA for customer shopping experience" {
                technology "React 19.1.1, React Router, Bootstrap 5"
                tags "Web Application"
                
                homeComponent = component "Home Component" "Landing page with featured products and categories"
                productComponent = component "Product Components" "Product listing, detail, and search functionality" 
                cartComponent = component "Shopping Cart" "Cart management and checkout process"
                authComponent = component "Authentication" "Login, register and OAuth2 integration"
                chatComponent = component "Chat Interface" "Real-time customer support chat"
                profileComponent = component "User Profile" "Account management and order history"
            }
            
            adminWeb = container "Admin Web Interface" "Server-side rendered admin panel" {
                technology "Thymeleaf, Bootstrap 5, Spring MVC"
                tags "Web Application"
                
                dashboardController = component "Dashboard Controller" "Admin dashboard with analytics"
                productController = component "Product Management" "CRUD operations for products and categories"
                orderController = component "Order Management" "Order processing and fulfillment"
                customerController = component "Customer Management" "User management and support"
                reportController = component "Reports & Analytics" "Business intelligence and reporting"
            }
            
            # Backend Container
            apiBackend = container "Spring Boot Backend" "Core business logic and API services" {
                technology "Spring Boot 3.5.3, Java 21, Spring Security 6"
                tags "Application"
                
                # API Controllers
                authController = component "Authentication Controller" "JWT and OAuth2 authentication APIs"
                productApiController = component "Product API Controller" "Product catalog REST APIs"
                cartApiController = component "Cart API Controller" "Shopping cart management APIs"
                checkoutApiController = component "Checkout API Controller" "Order processing and payment APIs"
                chatApiController = component "Chat API Controller" "Real-time messaging APIs"
                adminApiController = component "Admin API Controller" "Administrative operations APIs"
                
                # Services  
                userService = component "User Service" "User management and authentication business logic"
                productService = component "Product Service" "Product catalog management"
                cartService = component "Cart Service" "Shopping cart operations"
                orderService = component "Order Service" "Order processing and management"
                paymentService = component "Payment Service" "Payment processing with MoMo integration"
                chatService = component "Chat Service" "Real-time messaging with Firebase"
                reportService = component "Report Service" "Analytics and reporting"
                
                # Repositories
                userRepository = component "User Repository" "User data access layer"
                productRepository = component "Product Repository" "Product data access"
                orderRepository = component "Order Repository" "Order data management"
                cartRepository = component "Cart Repository" "Shopping cart persistence"
            }
            
            # Database Container
            database = container "MySQL Database" "Primary data store for all application data" {
                technology "MySQL 8.0"
                tags "Database"
                
                userTables = component "User Tables" "users, roles, authentication data"
                productTables = component "Product Tables" "products, categories, ingredients, images"
                orderTables = component "Order Tables" "orders, order_items, payments"
                cartTables = component "Cart Tables" "carts, cart_items"
                chatTables = component "Chat Tables" "chat_rooms, messages"
            }
        }

        # User Relationships
        customer -> cosmeticStore.customerWeb "Browses products, places orders, chats with support"
        admin -> cosmeticStore.adminWeb "Manages products, processes orders, provides support"
        
        # Frontend to Backend Relationships
        cosmeticStore.customerWeb -> cosmeticStore.apiBackend "Makes API calls" "HTTPS/REST"
        cosmeticStore.adminWeb -> cosmeticStore.apiBackend "Uses services" "Direct method calls"
        
        # Backend to Database
        cosmeticStore.apiBackend -> cosmeticStore.database "Reads from and writes to" "JPA/Hibernate"
        
        # External System Integrations
        cosmeticStore.apiBackend -> momoPayment "Processes payments" "HTTPS/REST API"
        cosmeticStore.apiBackend -> firebaseService "Manages real-time chat" "Firebase SDK"
        cosmeticStore.apiBackend -> cloudinaryService "Uploads and manages images" "Cloudinary API"
        cosmeticStore.customerWeb -> googleOAuth "Social login" "OAuth2"
        cosmeticStore.customerWeb -> firebaseService "Real-time chat messaging" "Firebase SDK"
        
        # Component Level Relationships
        
        # Customer Web Components
        cosmeticStore.customerWeb.authComponent -> cosmeticStore.apiBackend.authController "Authentication requests"
        cosmeticStore.customerWeb.productComponent -> cosmeticStore.apiBackend.productApiController "Product data requests"
        cosmeticStore.customerWeb.cartComponent -> cosmeticStore.apiBackend.cartApiController "Cart operations"
        cosmeticStore.customerWeb.cartComponent -> cosmeticStore.apiBackend.checkoutApiController "Order placement"
        cosmeticStore.customerWeb.chatComponent -> cosmeticStore.apiBackend.chatApiController "Chat messages"
        
        # Admin Web Components  
        cosmeticStore.adminWeb.productController -> cosmeticStore.apiBackend.productService "Product management"
        cosmeticStore.adminWeb.orderController -> cosmeticStore.apiBackend.orderService "Order processing"
        cosmeticStore.adminWeb.dashboardController -> cosmeticStore.apiBackend.reportService "Analytics data"
        
        # Backend Services to Repositories
        cosmeticStore.apiBackend.userService -> cosmeticStore.apiBackend.userRepository "User data operations"
        cosmeticStore.apiBackend.productService -> cosmeticStore.apiBackend.productRepository "Product data operations"
        cosmeticStore.apiBackend.orderService -> cosmeticStore.apiBackend.orderRepository "Order data operations"
        cosmeticStore.apiBackend.cartService -> cosmeticStore.apiBackend.cartRepository "Cart data operations"
        
        # Repositories to Database Tables
        cosmeticStore.apiBackend.userRepository -> cosmeticStore.database.userTables "CRUD operations"
        cosmeticStore.apiBackend.productRepository -> cosmeticStore.database.productTables "CRUD operations"
        cosmeticStore.apiBackend.orderRepository -> cosmeticStore.database.orderTables "CRUD operations"
        cosmeticStore.apiBackend.cartRepository -> cosmeticStore.database.cartTables "CRUD operations"
    }

    views {
        # System Context Diagram
        systemContext cosmeticStore "SystemContext" "System Context diagram for Cosmetic Store Platform" {
            include *
            exclude "element.tag==Component"
            autolayout lr
        }
        
        # Container Diagram
        container cosmeticStore "Containers" "Container diagram showing the high-level technology choices" {
            include *
            exclude "element.tag==Component"  
            autolayout lr
        }
        
        # Component Diagram - Customer Web App
        component cosmeticStore.customerWeb "CustomerWebComponents" "Component diagram for Customer Web Application" {
            include *
            include cosmeticStore.apiBackend.authController
            include cosmeticStore.apiBackend.productApiController  
            include cosmeticStore.apiBackend.cartApiController
            include cosmeticStore.apiBackend.checkoutApiController
            include cosmeticStore.apiBackend.chatApiController
            include firebaseService
            include googleOAuth
            autolayout lr
        }
        
        # Component Diagram - Admin Web Interface
        component cosmeticStore.adminWeb "AdminWebComponents" "Component diagram for Admin Web Interface" {
            include *
            include cosmeticStore.apiBackend.userService
            include cosmeticStore.apiBackend.productService
            include cosmeticStore.apiBackend.orderService  
            include cosmeticStore.apiBackend.reportService
            autolayout lr
        }
        
        # Component Diagram - Backend Services
        component cosmeticStore.apiBackend "BackendComponents" "Component diagram for Spring Boot Backend" {
            include *
            include cosmeticStore.database
            include momoPayment
            include firebaseService
            include cloudinaryService
            autolayout lr
        }

        styles {
            element "Element" {
                color #1168bd
                stroke #1168bd  
                strokeWidth 2
                shape roundedbox
            }
            element "Person" {
                background #08427b
                color #ffffff
                shape person
            }
            element "External System" {
                background #999999
                color #ffffff
            }
            element "Web Application" {
                background #1168bd
                color #ffffff
                shape webBrowser
            }
            element "Application" {
                background #1168bd
                color #ffffff
            }
            element "Database" {
                background #1168bd  
                color #ffffff
                shape cylinder
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
            relationship "Relationship" {
                thickness 2
                color #707070
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}