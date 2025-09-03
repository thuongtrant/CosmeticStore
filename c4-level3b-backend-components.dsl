workspace "Cosmetic Store - Backend Components" "Level 3B: Component diagram for Spring Boot API Backend showing layered architecture" {

    !identifiers hierarchical

    model {
        # External Systems
        customerWebApp = softwareSystem "Customer Web App" "React SPA for customers" {
            tags "External System"
        }
        adminWebApp = softwareSystem "Admin Web Interface" "Thymeleaf admin panel" {
            tags "External System"
        }
        mysqlDatabase = softwareSystem "MySQL Database" "Primary data store" {
            tags "Database"
        }
        firebaseService = softwareSystem "Firebase" "Real-time messaging service" {
            tags "External System"
        }
        momoPayment = softwareSystem "MoMo Payment Gateway" "Payment processing service" {
            tags "External System"
        }
        cloudinaryService = softwareSystem "Cloudinary" "Image management service" {
            tags "External System"
        }
        googleOAuth = softwareSystem "Google OAuth2" "Social authentication" {
            tags "External System"
        }
        facebookOAuth = softwareSystem "Facebook OAuth2" "Social authentication" {
            tags "External System"
        }
        emailService = softwareSystem "Email Service" "SMTP email delivery" {
            tags "External System"
        }
        
        # Main Backend Container
        cosmeticStore = softwareSystem "BFYStore Platform" {
            apiBackend = container "Spring Boot API Backend" "Core business logic and API services" {
                
                # ===== CONTROLLER LAYER =====
                # API Controllers (REST endpoints for React frontend)
                authController = component "AuthController" "Handles user authentication, registration, and JWT token management" {
                    technology "Spring Web, JWT, BCrypt"
                    tags "API Controller,Authentication"
                }
                productApiController = component "ApiProductController" "REST API for product catalog operations" {
                    technology "Spring Web, Pagination"
                    tags "API Controller,Product"
                }
                cartApiController = component "ApiCartController" "Shopping cart management API endpoints" {
                    technology "Spring Web, Session Management"
                    tags "API Controller,Commerce"
                }
                checkoutApiController = component "ApiCheckoutController" "Order processing and payment API" {
                    technology "Spring Web, Transaction Management"
                    tags "API Controller,Commerce"
                }
                momoApiController = component "ApiMoMoController" "MoMo payment integration endpoints" {
                    technology "Spring Web, Payment Processing"
                    tags "API Controller,Payment"
                }
                chatApiController = component "ChatApiController" "Real-time chat API for customer support" {
                    technology "Spring Web, Firebase Integration"
                    tags "API Controller,Communication"
                }
                userApiController = component "ApiUserController" "User profile and account management API" {
                    technology "Spring Web, User Management"
                    tags "API Controller,User"
                }
                filterApiController = component "ApiFilterController" "Product filtering and search API" {
                    technology "Spring Web, Search Algorithms"
                    tags "API Controller,Search"
                }
                shippingAddressController = component "ShippingAddressController" "Delivery address management API" {
                    technology "Spring Web, Address Validation"
                    tags "API Controller,User"
                }
                oauth2Controller = component "OAuth2Controller" "Social login integration endpoints" {
                    technology "Spring Security OAuth2, JWT"
                    tags "API Controller,Authentication"
                }
                policyController = component "PolicyController" "Privacy policy and terms API" {
                    technology "Spring Web, Static Content"
                    tags "API Controller,Info"
                }
                firebaseController = component "FirebaseController" "Firebase configuration and management API" {
                    technology "Firebase Admin SDK"
                    tags "API Controller,Configuration"
                }
                
                # Admin Controllers (MVC for Thymeleaf templates)
                webController = component "WebController (Admin)" "Main admin web controller for login/dashboard navigation" {
                    technology "Spring MVC, Thymeleaf"
                    tags "MVC Controller,Admin"
                }
                productController = component "ProductController (Admin)" "Admin product management web interface" {
                    technology "Spring MVC, Thymeleaf, File Upload"
                    tags "MVC Controller,Admin,Product"
                }
                orderController = component "OrderController (Admin)" "Admin order processing web interface" {
                    technology "Spring MVC, Thymeleaf, Order Management"
                    tags "MVC Controller,Admin,Commerce"
                }
                categoryController = component "CategoryController (Admin)" "Admin category management web interface" {
                    technology "Spring MVC, Thymeleaf"
                    tags "MVC Controller,Admin,Product"
                }
                reportController = component "ReportController (Admin)" "Admin analytics and reporting web interface" {
                    technology "Spring MVC, Thymeleaf, Chart Generation"
                    tags "MVC Controller,Admin,Analytics"
                }
                chatController = component "ChatController (Admin)" "Admin chat support web interface" {
                    technology "Spring MVC, Thymeleaf, Firebase Integration"
                    tags "MVC Controller,Admin,Communication"
                }
                adminChatApiController = component "AdminChatApiController" "Admin chat operations API" {
                    technology "Spring Web, Firebase Integration"
                    tags "API Controller,Admin,Communication"
                }
                
                # ===== SERVICE LAYER =====
                # Core Business Services
                userService = component "UserServiceImpl" "User authentication and account management business logic" {
                    technology "Spring Service, Password Encryption, User Validation"
                    tags "Business Service,User"
                }
                productService = component "ProductServiceImpl" "Product catalog management and business rules" {
                    technology "Spring Service, Business Logic, Validation"
                    tags "Business Service,Product"
                }
                cartService = component "CartServiceImpl" "Shopping cart operations and session management" {
                    technology "Spring Service, Session Management, Cart Logic"
                    tags "Business Service,Commerce"
                }
                orderService = component "OrderServiceImpl" "Order processing and fulfillment business logic" {
                    technology "Spring Service, Transaction Management, Order State Machine"
                    tags "Business Service,Commerce"
                }
                adminOrderService = component "AdminOrderServiceImpl" "Admin-specific order management operations" {
                    technology "Spring Service, Admin Operations, Order Status Updates"
                    tags "Business Service,Admin,Commerce"
                }
                chatService = component "ChatServiceImpl" "Real-time messaging and chat room management" {
                    technology "Spring Service, Firebase Integration, Message Processing"
                    tags "Business Service,Communication"
                }
                chatRateLimitService = component "ChatRateLimitService" "Chat message rate limiting and spam prevention" {
                    technology "Spring Service, Rate Limiting, Security"
                    tags "Business Service,Security,Communication"
                }
                
                # Payment and Transaction Services
                momoService = component "MoMoServiceImpl" "MoMo payment gateway integration service" {
                    technology "Spring Service, Payment Processing, HMAC Security"
                    tags "Business Service,Payment"
                }
                paymentProcessingService = component "PaymentProcessingServiceImpl" "Payment processing orchestration service" {
                    technology "Spring Service, Payment Orchestration, Transaction Management"
                    tags "Business Service,Payment"
                }
                paymentSessionService = component "PaymentSessionServiceImpl" "Payment session and state management" {
                    technology "Spring Service, Session Management, Payment State"
                    tags "Business Service,Payment"
                }
                
                # Reporting and Analytics Services
                reportService = component "ReportServiceImpl" "Business reporting and analytics generation" {
                    technology "Spring Service, Data Analysis, Report Generation"
                    tags "Business Service,Analytics"
                }
                reportDataService = component "ReportDataServiceImpl" "Report data aggregation and processing" {
                    technology "Spring Service, Data Processing, Aggregation"
                    tags "Business Service,Analytics"
                }
                chartDataService = component "ChartDataServiceImpl" "Chart and visualization data preparation" {
                    technology "Spring Service, Data Visualization, Chart APIs"
                    tags "Business Service,Analytics"
                }
                dateRangeCalculator = component "DateRangeCalculatorImpl" "Date range calculations for reporting" {
                    technology "Spring Service, Date Calculations, Time Series"
                    tags "Business Service,Utility"
                }
                
                # Supporting Services
                cloudinaryService = component "CloudinaryServiceImpl" "Image upload and management service" {
                    technology "Cloudinary SDK, Image Processing, CDN"
                    tags "Business Service,Media"
                }
                firebaseConfigService = component "FirebaseConfigService" "Firebase configuration and initialization" {
                    technology "Firebase Admin SDK, Configuration Management"
                    tags "Business Service,Configuration"
                }
                categoryService = component "CategoryServiceImpl" "Product category management service" {
                    technology "Spring Service, Category Operations"
                    tags "Business Service,Product"
                }
                ingredientService = component "IngredientServiceImpl" "Cosmetic ingredient management service" {
                    technology "Spring Service, Ingredient Database"
                    tags "Business Service,Product"
                }
                skinTypeService = component "SkinTypeServiceImpl" "Skin type classification service" {
                    technology "Spring Service, Skin Type Logic"
                    tags "Business Service,Product"
                }
                shippingAddressService = component "ShippingAddressServiceImpl" "Delivery address management service" {
                    technology "Spring Service, Address Validation, Geography"
                    tags "Business Service,User"
                }
                userAuthenticationService = component "UserAuthenticationServiceImpl" "Authentication and authorization service" {
                    technology "Spring Security, JWT, OAuth2, Session Management"
                    tags "Business Service,Authentication"
                }
                
                # ===== REPOSITORY LAYER =====
                # Data Access Repositories
                userRepository = component "UserRepository" "User data access and persistence operations" {
                    technology "Spring Data JPA, JPQL Queries"
                    tags "Repository,User"
                }
                productRepository = component "ProductRepository" "Product catalog data access" {
                    technology "Spring Data JPA, Custom Queries, Pagination"
                    tags "Repository,Product"
                }
                orderRepository = component "OrderRepository" "Order and order item data persistence" {
                    technology "Spring Data JPA, Transaction Support"
                    tags "Repository,Commerce"
                }
                cartRepository = component "CartRepository" "Shopping cart data access" {
                    technology "Spring Data JPA, Session Persistence"
                    tags "Repository,Commerce"
                }
                paymentRepository = component "PaymentRepository" "Payment transaction data persistence" {
                    technology "Spring Data JPA, Transaction Logging"
                    tags "Repository,Payment"
                }
                categoryRepository = component "CategoryRepository" "Product category data access" {
                    technology "Spring Data JPA, Hierarchical Queries"
                    tags "Repository,Product"
                }
                chatRoomRepository = component "ChatRoomRepository" "Chat room and message data persistence" {
                    technology "Spring Data JPA, Message History"
                    tags "Repository,Communication"
                }
                roleRepository = component "RoleRepository" "User role and permission data access" {
                    technology "Spring Data JPA, RBAC Support"
                    tags "Repository,User"
                }
                shippingAddressRepository = component "ShippingAddressRepository" "Delivery address data persistence" {
                    technology "Spring Data JPA, Geographic Queries"
                    tags "Repository,User"
                }
                ingredientRepository = component "IngredientRepository" "Cosmetic ingredient data access" {
                    technology "Spring Data JPA, Search Queries"
                    tags "Repository,Product"
                }
                
                # ===== CONFIGURATION LAYER =====
                # Security and Authentication Configuration
                webSecurityConfig = component "WebSecurityConfig" "Main security configuration for JWT, OAuth2, and CORS" {
                    technology "Spring Security, JWT Configuration, OAuth2 Setup"
                    tags "Configuration,Security"
                }
                authTokenFilter = component "AuthTokenFilter" "JWT token validation filter for API requests" {
                    technology "Spring Security Filter, JWT Processing"
                    tags "Configuration,Security,Filter"
                }
                jwtUtils = component "JwtUtils" "JWT token generation, validation, and parsing utilities" {
                    technology "JWT Library, Token Management"
                    tags "Configuration,Security,Utility"
                }
                authEntryPointJwt = component "AuthEntryPointJwt" "Unauthorized access handling for API endpoints" {
                    technology "Spring Security, Exception Handling"
                    tags "Configuration,Security"
                }
                oauth2AuthSuccessHandler = component "OAuth2AuthenticationSuccessHandler" "OAuth2 login success handling and JWT generation" {
                    technology "OAuth2 Success Handler, JWT Generation"
                    tags "Configuration,Authentication"
                }
                oauth2AuthFailureHandler = component "OAuth2AuthenticationFailureHandler" "OAuth2 login failure handling and error processing" {
                    technology "OAuth2 Failure Handler, Error Management"
                    tags "Configuration,Authentication"
                }
                
                # External Service Configuration
                firebaseConfig = component "FirebaseConfig" "Firebase Admin SDK configuration and initialization" {
                    technology "Firebase Admin SDK, Service Account"
                    tags "Configuration,External Service"
                }
                cloudinaryConfig = component "CloudinaryConfig" "Cloudinary image service configuration" {
                    technology "Cloudinary SDK, API Configuration"
                    tags "Configuration,External Service"
                }
                momoConfig = component "MoMoConfig" "MoMo payment gateway configuration and credentials" {
                    technology "MoMo API Configuration, Security Keys"
                    tags "Configuration,Payment"
                }
                
                # Application Configuration
                appConfig = component "AppConfig" "General application configuration and bean definitions" {
                    technology "Spring Configuration, Bean Management"
                    tags "Configuration,Application"
                }
                tomcatConfig = component "TomcatConfig" "Embedded Tomcat server configuration" {
                    technology "Spring Boot Tomcat, Server Configuration"
                    tags "Configuration,Server"
                }
                
                # ===== UTILITY AND SUPPORT COMPONENTS =====
                # Exception Handling
                globalExceptionHandler = component "GlobalExceptionHandler" "Global exception handling and error response formatting" {
                    technology "Spring Exception Handling, Error Processing"
                    tags "Utility,Exception Handling"
                }
                
                # Custom Exceptions
                orderException = component "OrderException" "Custom exception for order processing errors" {
                    technology "Custom Exception Class"
                    tags "Utility,Exception"
                }
                orderProcessingException = component "OrderProcessingException" "Custom exception for order processing failures" {
                    technology "Custom Exception Class"
                    tags "Utility,Exception"
                }
                
                # Validation
                checkoutRequestValidator = component "CheckoutRequestValidator" "Custom validator for checkout request data" {
                    technology "Spring Validation, Custom Validators"
                    tags "Utility,Validation"
                }
                
                # Utilities
                orderNumberGenerator = component "OrderNumberGenerator" "Unique order number generation utility" {
                    technology "Number Generation Algorithm"
                    tags "Utility,Generator"
                }
                
                # DTOs (Data Transfer Objects)
                authDTOs = component "Authentication DTOs" "Request/Response objects for authentication operations" {
                    technology "Java POJOs, Validation Annotations"
                    tags "DTO,Authentication"
                }
                productDTOs = component "Product DTOs" "Request/Response objects for product operations" {
                    technology "Java POJOs, Validation Annotations"
                    tags "DTO,Product"
                }
                orderDTOs = component "Order DTOs" "Request/Response objects for order operations" {
                    technology "Java POJOs, Validation Annotations"
                    tags "DTO,Commerce"
                }
                paymentDTOs = component "Payment DTOs" "Request/Response objects for payment operations" {
                    technology "Java POJOs, Validation Annotations"
                    tags "DTO,Payment"
                }
                
                # Entity Mappers
                userMapper = component "UserMapper" "Entity to DTO mapping for user objects" {
                    technology "MapStruct, Object Mapping"
                    tags "Mapper,User"
                }
                productMapper = component "ProductMapper" "Entity to DTO mapping for product objects" {
                    technology "MapStruct, Object Mapping"
                    tags "Mapper,Product"
                }
                orderMapper = component "OrderMapper" "Entity to DTO mapping for order objects" {
                    technology "MapStruct, Object Mapping"
                    tags "Mapper,Commerce"
                }
            }
        }

        # External system interactions
        customerWebApp -> cosmeticStore.apiBackend.authController "Authenticates users, manages JWT tokens" "HTTPS/REST"
        customerWebApp -> cosmeticStore.apiBackend.productApiController "Fetches products, categories, filters" "HTTPS/REST"
        customerWebApp -> cosmeticStore.apiBackend.cartApiController "Manages shopping cart operations" "HTTPS/REST"
        customerWebApp -> cosmeticStore.apiBackend.checkoutApiController "Processes orders and payments" "HTTPS/REST"
        customerWebApp -> cosmeticStore.apiBackend.chatApiController "Handles customer support chat" "HTTPS/REST"
        
        adminWebApp -> cosmeticStore.apiBackend.webController "Admin dashboard and navigation" "HTTP/MVC"
        adminWebApp -> cosmeticStore.apiBackend.productController "Product management interface" "HTTP/MVC"
        adminWebApp -> cosmeticStore.apiBackend.orderController "Order processing interface" "HTTP/MVC"
        adminWebApp -> cosmeticStore.apiBackend.reportController "Analytics and reporting interface" "HTTP/MVC"
        
        # Controller to Service interactions
        cosmeticStore.apiBackend.authController -> cosmeticStore.apiBackend.userAuthenticationService "Delegates authentication operations"
        cosmeticStore.apiBackend.productApiController -> cosmeticStore.apiBackend.productService "Handles product business logic"
        cosmeticStore.apiBackend.cartApiController -> cosmeticStore.apiBackend.cartService "Manages cart operations"
        cosmeticStore.apiBackend.checkoutApiController -> cosmeticStore.apiBackend.orderService "Processes order creation"
        cosmeticStore.apiBackend.checkoutApiController -> cosmeticStore.apiBackend.paymentProcessingService "Handles payment processing"
        cosmeticStore.apiBackend.momoApiController -> cosmeticStore.apiBackend.momoService "Manages MoMo payment integration"
        cosmeticStore.apiBackend.chatApiController -> cosmeticStore.apiBackend.chatService "Handles chat operations"
        
        # Service to Repository interactions
        cosmeticStore.apiBackend.userAuthenticationService -> cosmeticStore.apiBackend.userRepository "Accesses user data"
        cosmeticStore.apiBackend.productService -> cosmeticStore.apiBackend.productRepository "Accesses product data"
        cosmeticStore.apiBackend.orderService -> cosmeticStore.apiBackend.orderRepository "Persists order data"
        cosmeticStore.apiBackend.cartService -> cosmeticStore.apiBackend.cartRepository "Manages cart persistence"
        cosmeticStore.apiBackend.momoService -> cosmeticStore.apiBackend.paymentRepository "Logs payment transactions"
        cosmeticStore.apiBackend.chatService -> cosmeticStore.apiBackend.chatRoomRepository "Manages chat data"
        
        # Repository to Database interactions
        cosmeticStore.apiBackend.userRepository -> mysqlDatabase "Performs user CRUD operations" "JDBC/MySQL"
        cosmeticStore.apiBackend.productRepository -> mysqlDatabase "Performs product CRUD operations" "JDBC/MySQL"
        cosmeticStore.apiBackend.orderRepository -> mysqlDatabase "Performs order CRUD operations" "JDBC/MySQL"
        cosmeticStore.apiBackend.cartRepository -> mysqlDatabase "Performs cart CRUD operations" "JDBC/MySQL"
        cosmeticStore.apiBackend.paymentRepository -> mysqlDatabase "Performs payment CRUD operations" "JDBC/MySQL"
        
        # External service integrations
        cosmeticStore.apiBackend.momoService -> momoPayment "Processes payments, handles callbacks" "HTTPS/REST"
        cosmeticStore.apiBackend.chatService -> firebaseService "Manages real-time messaging" "Firebase SDK"
        cosmeticStore.apiBackend.cloudinaryService -> cloudinaryService "Uploads and manages images" "HTTPS/REST"
        cosmeticStore.apiBackend.oauth2Controller -> googleOAuth "Handles Google social login" "OAuth2"
        cosmeticStore.apiBackend.oauth2Controller -> facebookOAuth "Handles Facebook social login" "OAuth2"
        cosmeticStore.apiBackend.userAuthenticationService -> emailService "Sends password reset emails" "SMTP"
        
        # Configuration interactions
        cosmeticStore.apiBackend.webSecurityConfig -> cosmeticStore.apiBackend.authTokenFilter "Configures JWT filter chain"
        cosmeticStore.apiBackend.authTokenFilter -> cosmeticStore.apiBackend.jwtUtils "Validates JWT tokens"
        cosmeticStore.apiBackend.authController -> cosmeticStore.apiBackend.jwtUtils "Generates JWT tokens"
        cosmeticStore.apiBackend.firebaseConfigService -> cosmeticStore.apiBackend.firebaseConfig "Uses Firebase configuration"
        cosmeticStore.apiBackend.cloudinaryService -> cosmeticStore.apiBackend.cloudinaryConfig "Uses Cloudinary configuration"
        cosmeticStore.apiBackend.momoService -> cosmeticStore.apiBackend.momoConfig "Uses MoMo payment configuration"
    }

    views {
        component cosmeticStore.apiBackend "BackendAPI-Components" "Component diagram for Spring Boot API Backend showing layered architecture" {
            include *
            exclude customerWebApp adminWebApp mysqlDatabase firebaseService momoPayment cloudinaryService googleOAuth facebookOAuth emailService
            title "Spring Boot API Backend - Component Diagram"
            description "This diagram shows the internal components of the Spring Boot backend, organized in layers: Controllers, Services, Repositories, Configuration, and supporting utilities."
            autolayout lr
        }

        styles {
            element "Element" {
                color #000000
                stroke #cccccc
                strokeWidth 1
                shape roundedbox
                fontSize 9
            }
            element "External System" {
                background #999999
                color #ffffff
                fontSize 8
            }
            element "Database" {
                background #3ca2c4
                color #ffffff
                shape cylinder
            }
            
            # Controller Layer Styles
            element "API Controller" {
                background #1168bd
                color #ffffff
                fontSize 9
            }
            element "MVC Controller" {
                background #2980b9
                color #ffffff
                fontSize 9
            }
            element "Authentication" {
                background #e74c3c
                color #ffffff
            }
            element "Product" {
                background #27ae60
                color #ffffff
            }
            element "Commerce" {
                background #f39c12
                color #ffffff
            }
            element "Payment" {
                background #d35400
                color #ffffff
            }
            element "Communication" {
                background #16a085
                color #ffffff
            }
            element "User" {
                background #9b59b6
                color #ffffff
            }
            element "Search" {
                background #34495e
                color #ffffff
            }
            element "Info" {
                background #7f8c8d
                color #ffffff
            }
            element "Admin" {
                background #2c3e50
                color #ffffff
            }
            element "Analytics" {
                background #8e44ad
                color #ffffff
            }
            
            # Service Layer Styles
            element "Business Service" {
                background #3498db
                color #ffffff
                fontSize 9
                shape roundedbox
            }
            element "Security" {
                background #c0392b
                color #ffffff
            }
            element "Media" {
                background #e67e22
                color #ffffff
            }
            element "Utility" {
                background #95a5a6
                color #ffffff
            }
            
            # Repository Layer Styles
            element "Repository" {
                background #2ecc71
                color #ffffff
                fontSize 9
                shape database
            }
            
            # Configuration Layer Styles
            element "Configuration" {
                background #f1c40f
                color #000000
                fontSize 9
                shape component
            }
            element "Filter" {
                background #d68910
                color #ffffff
            }
            element "External Service" {
                background #a04000
                color #ffffff
            }
            element "Application" {
                background #b7950b
                color #ffffff
            }
            element "Server" {
                background #7d6608
                color #ffffff
            }
            
            # Utility Styles
            element "Exception Handling" {
                background #dc3545
                color #ffffff
            }
            element "Exception" {
                background #bd2130
                color #ffffff
            }
            element "Validation" {
                background #17a2b8
                color #ffffff
            }
            element "Generator" {
                background #6c757d
                color #ffffff
            }
            element "DTO" {
                background #fd7e14
                color #ffffff
                fontSize 8
                shape folder
            }
            element "Mapper" {
                background #20c997
                color #ffffff
                fontSize 8
            }
            
            relationship "Relationship" {
                thickness 1
                color #707070
                fontSize 8
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}