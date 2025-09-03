workspace "Cosmetic Store - Admin Components" "Level 3C: Component diagram for Admin Web Interface (Thymeleaf)" {

    !identifiers hierarchical

    model {
        # Actors
        admin = person "Administrator" "Store administrators managing the platform"
        
        # External Systems
        springBootBackend = softwareSystem "Spring Boot Backend" "API and service layer" {
            tags "Backend API"
        }
        
        # Main System
        cosmeticStore = softwareSystem "BFYStore Platform" {
            
            # Admin Web Application Container with Components
            adminWebApp = container "Admin Web Interface" "Server-side rendered admin panel for store management" {
                
                # ===== THYMELEAF TEMPLATES =====
                # Authentication Templates
                loginTemplate = component "login.html" "Admin login page template with Spring Security integration" {
                    technology "Thymeleaf, Bootstrap 5, Spring Security"
                    tags "Template,Authentication"
                }
                registerTemplate = component "register.html" "Admin registration template with role-based signup" {
                    technology "Thymeleaf, Form Validation, Bootstrap"
                    tags "Template,Authentication"
                }
                
                # Core Admin Templates
                baseTemplate = component "base.html" "Master template with common layout, navigation, and styling" {
                    technology "Thymeleaf Layout Dialect, Bootstrap 5, Font Awesome"
                    tags "Template,Layout"
                }
                dashboardTemplate = component "dashboard.html" "Main admin dashboard with analytics overview and KPIs" {
                    technology "Thymeleaf, Chart.js, Bootstrap Cards"
                    tags "Template,Dashboard"
                }
                indexTemplate = component "index.html" "Admin home page with system status and quick actions" {
                    technology "Thymeleaf, Bootstrap Dashboard Layout"
                    tags "Template,Home"
                }
                
                # Product Management Templates
                productTemplate = component "product.html" "Product listing page with search, filter, and bulk operations" {
                    technology "Thymeleaf, DataTables, Image Gallery"
                    tags "Template,Product"
                }
                productFormTemplate = component "product-form.html" "Product creation and editing form with image upload" {
                    technology "Thymeleaf, File Upload, Form Validation"
                    tags "Template,Product,Form"
                }
                categoryTemplate = component "category.html" "Category management page with hierarchical display" {
                    technology "Thymeleaf, Tree Structure, Modal Forms"
                    tags "Template,Category"
                }
                categoryFormTemplate = component "category-form.html" "Category creation and editing form" {
                    technology "Thymeleaf, Form Components, Validation"
                    tags "Template,Category,Form"
                }
                
                # Order Management Templates
                ordersTemplate = component "orders.html" "Order listing page with status filters and bulk actions" {
                    technology "Thymeleaf, Status Badges, Action Buttons"
                    tags "Template,Order"
                }
                orderDetailTemplate = component "order-detail.html" "Detailed order view with items, customer info, and status updates" {
                    technology "Thymeleaf, Order Timeline, Print Layout"
                    tags "Template,Order,Detail"
                }
                
                # Customer Management Templates
                customerDashboardTemplate = component "customer-dashboard.html" "Customer management dashboard with user analytics" {
                    technology "Thymeleaf, User Statistics, Search Functions"
                    tags "Template,Customer"
                }
                
                # Communication Templates
                chatTemplate = component "Chat.html" "Admin chat interface for customer support with real-time messaging" {
                    technology "Thymeleaf, Firebase Integration, WebSocket UI"
                    tags "Template,Communication"
                }
                
                # Analytics and Reporting Templates
                reportsTemplate = component "reports.html" "Comprehensive reporting dashboard with charts and export options" {
                    technology "Thymeleaf, Chart.js, Export Functions, Date Pickers"
                    tags "Template,Analytics"
                }
                
                # Error and Utility Templates
                errorTemplate = component "error.html" "Error page template with user-friendly error messages" {
                    technology "Thymeleaf, Error Handling, Responsive Design"
                    tags "Template,Error"
                }
                forgotPasswordTemplate = component "forgot-password.html" "Password reset request template" {
                    technology "Thymeleaf, Email Integration, Security Forms"
                    tags "Template,Security"
                }
                
                # ===== MVC CONTROLLERS =====
                # Main Web Controller
                webController = component "WebController" "Main admin web controller handling authentication and navigation" {
                    technology "Spring MVC, Spring Security, Model Management"
                    tags "MVC Controller,Authentication"
                }
                
                # Product Management Controllers
                productController = component "ProductController" "Admin product management controller with CRUD operations" {
                    technology "Spring MVC, File Upload, Image Processing"
                    tags "MVC Controller,Product"
                }
                categoryController = component "CategoryController" "Category management controller with hierarchical operations" {
                    technology "Spring MVC, Tree Operations, Validation"
                    tags "MVC Controller,Category"
                }
                
                # Order Management Controllers
                orderController = component "OrderController" "Order processing and management controller" {
                    technology "Spring MVC, Order Status Management, PDF Generation"
                    tags "MVC Controller,Order"
                }
                
                # Analytics and Reporting Controllers
                reportController = component "ReportController" "Analytics and reporting controller with data aggregation" {
                    technology "Spring MVC, Chart Data Generation, Export Functions"
                    tags "MVC Controller,Analytics"
                }
                
                # Communication Controllers
                chatController = component "ChatController" "Admin chat support controller with Firebase integration" {
                    technology "Spring MVC, Firebase Admin SDK, Real-time Updates"
                    tags "MVC Controller,Communication"
                }
                
                # ===== STATIC RESOURCES =====
                # CSS and Styling
                adminCSS = component "Admin CSS Styles" "Custom styling for admin interface with responsive design" {
                    technology "CSS3, Bootstrap 5 Customization, Font Awesome"
                    tags "Static Resource,Styling"
                }
                
                # JavaScript and Client-side Logic
                adminJS = component "Admin JavaScript" "Client-side functionality for admin interface" {
                    technology "JavaScript, jQuery, Chart.js, DataTables"
                    tags "Static Resource,JavaScript"
                }
                chartJS = component "Chart Components" "Interactive charts for analytics and reporting" {
                    technology "Chart.js, Data Visualization, Real-time Updates"
                    tags "Static Resource,Visualization"
                }
                
                # Images and Assets
                adminAssets = component "Admin Assets" "Images, icons, and other static assets for admin interface" {
                    technology "Image Files, Icons, Logos, Static Content"
                    tags "Static Resource,Assets"
                }
                
                # ===== CONFIGURATION AND SECURITY =====
                # Template Configuration
                thymeleafConfig = component "Thymeleaf Configuration" "Template engine configuration with Spring Security integration" {
                    technology "Thymeleaf Configuration, Spring Security Integration"
                    tags "Configuration,Template Engine"
                }
                
                # Security Integration
                springSecurityIntegration = component "Spring Security Integration" "Security integration for admin panel with role-based access" {
                    technology "Spring Security, CSRF Protection, Session Management"
                    tags "Configuration,Security"
                }
                
                # ===== DATA MODELS FOR VIEWS =====
                # View Models
                dashboardModel = component "Dashboard View Model" "Data model for dashboard with KPIs and analytics" {
                    technology "Spring Model, Data Aggregation"
                    tags "View Model,Dashboard"
                }
                productViewModel = component "Product View Model" "Data model for product management views" {
                    technology "Spring Model, Product Data, Pagination"
                    tags "View Model,Product"
                }
                orderViewModel = component "Order View Model" "Data model for order management views" {
                    technology "Spring Model, Order Data, Status Information"
                    tags "View Model,Order"
                }
                reportViewModel = component "Report View Model" "Data model for analytics and reporting views" {
                    technology "Spring Model, Chart Data, Export Data"
                    tags "View Model,Analytics"
                }
            }
        }

        # Admin user interactions
        admin -> cosmeticStore.adminWebApp.loginTemplate "Accesses admin login page"
        admin -> cosmeticStore.adminWebApp.dashboardTemplate "Views admin dashboard and KPIs"
        admin -> cosmeticStore.adminWebApp.productTemplate "Manages product catalog"
        admin -> cosmeticStore.adminWebApp.ordersTemplate "Processes and manages orders"
        admin -> cosmeticStore.adminWebApp.chatTemplate "Provides customer support via chat"
        admin -> cosmeticStore.adminWebApp.reportsTemplate "Views analytics and generates reports"
        
        # Template relationships and includes
        cosmeticStore.adminWebApp.baseTemplate -> cosmeticStore.adminWebApp.adminCSS "Includes styling and layout"
        cosmeticStore.adminWebApp.baseTemplate -> cosmeticStore.adminWebApp.adminJS "Includes client-side functionality"
        cosmeticStore.adminWebApp.dashboardTemplate -> cosmeticStore.adminWebApp.baseTemplate "Extends base layout"
        cosmeticStore.adminWebApp.productTemplate -> cosmeticStore.adminWebApp.baseTemplate "Extends base layout"
        cosmeticStore.adminWebApp.ordersTemplate -> cosmeticStore.adminWebApp.baseTemplate "Extends base layout"
        cosmeticStore.adminWebApp.chatTemplate -> cosmeticStore.adminWebApp.baseTemplate "Extends base layout"
        cosmeticStore.adminWebApp.reportsTemplate -> cosmeticStore.adminWebApp.baseTemplate "Extends base layout"
        
        # Template to Chart Integration
        cosmeticStore.adminWebApp.dashboardTemplate -> cosmeticStore.adminWebApp.chartJS "Displays dashboard charts"
        cosmeticStore.adminWebApp.reportsTemplate -> cosmeticStore.adminWebApp.chartJS "Displays analytics charts"
        
        # Controller to Template relationships (Spring MVC pattern)
        cosmeticStore.adminWebApp.webController -> cosmeticStore.adminWebApp.loginTemplate "Renders login page"
        cosmeticStore.adminWebApp.webController -> cosmeticStore.adminWebApp.dashboardTemplate "Renders dashboard"
        cosmeticStore.adminWebApp.productController -> cosmeticStore.adminWebApp.productTemplate "Renders product management"
        cosmeticStore.adminWebApp.productController -> cosmeticStore.adminWebApp.productFormTemplate "Renders product forms"
        cosmeticStore.adminWebApp.categoryController -> cosmeticStore.adminWebApp.categoryTemplate "Renders category management"
        cosmeticStore.adminWebApp.orderController -> cosmeticStore.adminWebApp.ordersTemplate "Renders order management"
        cosmeticStore.adminWebApp.orderController -> cosmeticStore.adminWebApp.orderDetailTemplate "Renders order details"
        cosmeticStore.adminWebApp.reportController -> cosmeticStore.adminWebApp.reportsTemplate "Renders analytics reports"
        cosmeticStore.adminWebApp.chatController -> cosmeticStore.adminWebApp.chatTemplate "Renders chat interface"
        
        # Controller to View Model relationships
        cosmeticStore.adminWebApp.webController -> cosmeticStore.adminWebApp.dashboardModel "Prepares dashboard data"
        cosmeticStore.adminWebApp.productController -> cosmeticStore.adminWebApp.productViewModel "Prepares product data"
        cosmeticStore.adminWebApp.orderController -> cosmeticStore.adminWebApp.orderViewModel "Prepares order data"
        cosmeticStore.adminWebApp.reportController -> cosmeticStore.adminWebApp.reportViewModel "Prepares report data"
        
        # View Model to Template data binding
        cosmeticStore.adminWebApp.dashboardModel -> cosmeticStore.adminWebApp.dashboardTemplate "Provides dashboard data"
        cosmeticStore.adminWebApp.productViewModel -> cosmeticStore.adminWebApp.productTemplate "Provides product data"
        cosmeticStore.adminWebApp.orderViewModel -> cosmeticStore.adminWebApp.ordersTemplate "Provides order data"
        cosmeticStore.adminWebApp.reportViewModel -> cosmeticStore.adminWebApp.reportsTemplate "Provides analytics data"
        
        # Configuration relationships
        cosmeticStore.adminWebApp.thymeleafConfig -> cosmeticStore.adminWebApp.baseTemplate "Configures template engine"
        cosmeticStore.adminWebApp.springSecurityIntegration -> cosmeticStore.adminWebApp.webController "Provides security context"
        cosmeticStore.adminWebApp.springSecurityIntegration -> cosmeticStore.adminWebApp.loginTemplate "Handles authentication"
        
        # Backend service interactions
        cosmeticStore.adminWebApp.webController -> springBootBackend "Calls authentication services" "Spring Internal"
        cosmeticStore.adminWebApp.productController -> springBootBackend "Calls product services" "Spring Internal"
        cosmeticStore.adminWebApp.orderController -> springBootBackend "Calls order services" "Spring Internal"
        cosmeticStore.adminWebApp.reportController -> springBootBackend "Calls reporting services" "Spring Internal"
        cosmeticStore.adminWebApp.chatController -> springBootBackend "Calls chat services" "Spring Internal"
    }

    views {
        component cosmeticStore.adminWebApp "AdminWebApp-Components" "Component diagram for Admin Web Interface showing MVC pattern with Thymeleaf" {
            include *
            exclude springBootBackend
            title "Admin Web Interface - Component Diagram"
            description "This diagram shows the internal components of the Admin Web Interface using Spring MVC with Thymeleaf templates, including controllers, templates, static resources, and configuration."
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
            element "Person" {
                background #08427b
                color #ffffff
                shape person
                fontSize 11
            }
            element "Backend API" {
                background #666666
                color #ffffff
                fontSize 9
            }
            
            # Template Styles
            element "Template" {
                background #2ecc71
                color #ffffff
                fontSize 9
                shape document
            }
            element "Layout" {
                background #27ae60
                color #ffffff
            }
            element "Authentication" {
                background #e74c3c
                color #ffffff
            }
            element "Dashboard" {
                background #3498db
                color #ffffff
            }
            element "Home" {
                background #2980b9
                color #ffffff
            }
            element "Product" {
                background #f39c12
                color #ffffff
            }
            element "Category" {
                background #d68910
                color #ffffff
            }
            element "Order" {
                background #e67e22
                color #ffffff
            }
            element "Customer" {
                background #9b59b6
                color #ffffff
            }
            element "Communication" {
                background #16a085
                color #ffffff
            }
            element "Analytics" {
                background #8e44ad
                color #ffffff
            }
            element "Error" {
                background #e74c3c
                color #ffffff
            }
            element "Security" {
                background #c0392b
                color #ffffff
            }
            element "Form" {
                background #f1c40f
                color #000000
            }
            element "Detail" {
                background #d4a574
                color #ffffff
            }
            
            # Controller Styles
            element "MVC Controller" {
                background #34495e
                color #ffffff
                fontSize 9
                shape roundedbox
            }
            
            # Static Resource Styles
            element "Static Resource" {
                background #95a5a6
                color #ffffff
                fontSize 9
                shape folder
            }
            element "Styling" {
                background #7f8c8d
                color #ffffff
            }
            element "JavaScript" {
                background #f4d03f
                color #000000
            }
            element "Visualization" {
                background #af7ac5
                color #ffffff
            }
            element "Assets" {
                background #85929e
                color #ffffff
            }
            
            # Configuration Styles
            element "Configuration" {
                background #dc7633
                color #ffffff
                fontSize 9
                shape component
            }
            element "Template Engine" {
                background #cb4335
                color #ffffff
            }
            
            # View Model Styles
            element "View Model" {
                background #5dade2
                color #ffffff
                fontSize 9
                shape hexagon
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