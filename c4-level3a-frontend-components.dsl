workspace "Cosmetic Store - Frontend Components" "Level 3A: Component diagram for React Customer Web Application" {

    !identifiers hierarchical

    model {
        # Actors
        customer = person "Customer" "End users shopping for cosmetic products"
        
        # External Systems
        firebaseService = softwareSystem "Firebase Firestore" "Real-time messaging database" {
            tags "External System"
        }
        googleOAuth = softwareSystem "Google OAuth2" "Social authentication service" {
            tags "External System"
        }
        facebookOAuth = softwareSystem "Facebook OAuth2" "Social authentication service" {
            tags "External System"
        }
        
        # Main System
        cosmeticStore = softwareSystem "BFYStore Platform" {
            
            # Backend API (as external to frontend components)
            apiBackend = container "Spring Boot API Backend" "Business logic and data processing" {
                tags "Backend API"
            }
            
            # Customer Web App Container with Components
            customerWebApp = container "Customer Web Application" "React SPA for customer shopping experience" {
                
                # Layout Components
                headerComponent = component "Header Component" "Navigation bar with logo, menu, cart counter, and user authentication status" {
                    technology "React, Bootstrap"
                    tags "Layout,UI Component"
                }
                footerComponent = component "Footer Component" "Site footer with links, contact info, and social media" {
                    technology "React, Bootstrap"
                    tags "Layout,UI Component"
                }
                
                # Authentication Components
                loginComponent = component "Login Component" "User authentication form with email/password and social login options" {
                    technology "React, React Hook Form, Validation"
                    tags "Auth Component"
                }
                registerComponent = component "Register Component" "New user registration form with validation and role selection" {
                    technology "React, React Hook Form, Validation"
                    tags "Auth Component"
                }
                oauthRedirectComponent = component "OAuth2 Redirect Component" "Handles OAuth2 callback and JWT token processing" {
                    technology "React, JWT Processing"
                    tags "Auth Component"
                }
                
                # Product Components  
                homeComponent = component "Home Component" "Landing page with hero banner, featured products, and category highlights" {
                    technology "React, Bootstrap Carousel"
                    tags "Product Component,Page"
                }
                homePageComponent = component "HomePage Component" "Alternative home page layout with different product showcase" {
                    technology "React, Product Grid"
                    tags "Product Component,Page"
                }
                productDetailComponent = component "ProductDetail Component" "Detailed product view with images, description, ingredients, and add-to-cart" {
                    technology "React, Image Gallery, Product Info"
                    tags "Product Component,Page"
                }
                
                # E-commerce Components
                cartComponent = component "Cart Component" "Shopping cart with item management, quantity updates, and checkout initiation" {
                    technology "React, Cart State Management"
                    tags "Commerce Component,Page"
                }
                checkoutComponent = component "Checkout Component" "Order processing form with shipping details and payment options" {
                    technology "React, Form Validation, Payment Integration"
                    tags "Commerce Component,Page"
                }
                ordersComponent = component "Orders Component" "Customer order history and status tracking" {
                    technology "React, Order Status Display"
                    tags "Commerce Component,Page"
                }
                orderDetailComponent = component "OrderDetail Component" "Detailed view of specific order with items and tracking info" {
                    technology "React, Order Tracking"
                    tags "Commerce Component,Page"
                }
                
                # User Management Components
                profileComponent = component "Profile Component" "User account management and personal information editing" {
                    technology "React, User Form Management"
                    tags "User Component,Page"
                }
                shippingAddressComponent = component "ShippingAddress Component" "Delivery address management with map integration" {
                    technology "React, React Leaflet, Address Form"
                    tags "User Component,Page"
                }
                
                # Communication Components
                chatComponent = component "Chat Component" "Real-time customer support chat interface with message history" {
                    technology "React, Firebase SDK, Real-time Updates"
                    tags "Communication Component,Page"
                }
                
                # Payment Components
                momoReturnComponent = component "MoMoReturn Component" "Payment completion handler for MoMo payment gateway callbacks" {
                    technology "React, Payment Status Processing"
                    tags "Payment Component,Page"
                }
                
                # About/Information Components
                aboutBFYComponent = component "AboutBFY Component" "Company information and brand story page" {
                    technology "React, Static Content"
                    tags "Info Component,Page"
                }
                
                # State Management
                userContext = component "User Context Provider" "Global user authentication state management using React Context API" {
                    technology "React Context API, useReducer"
                    tags "State Management,Context"
                }
                cartContext = component "Cart Context Provider" "Global shopping cart state management with persistence" {
                    technology "React Context API, useReducer, LocalStorage"
                    tags "State Management,Context"
                }
                userReducer = component "User Reducer" "User state reducer for authentication actions and user data updates" {
                    technology "React useReducer Hook"
                    tags "State Management,Reducer"
                }
                cartReducer = component "Cart Reducer" "Cart state reducer for add/remove/update cart operations" {
                    technology "React useReducer Hook"
                    tags "State Management,Reducer"
                }
                
                # API and Services
                apiService = component "API Service (Apis.js)" "Centralized API configuration and endpoint definitions with Axios" {
                    technology "Axios, HTTP Client, JWT Token Management"
                    tags "API Service"
                }
                chatService = component "Chat Service" "Real-time messaging service wrapper for Firebase integration" {
                    technology "Firebase SDK, Real-time Messaging"
                    tags "External Service"
                }
                firebaseConfig = component "Firebase Config" "Firebase initialization and configuration for real-time features" {
                    technology "Firebase Web SDK, Firestore, Authentication"
                    tags "Configuration"
                }
            }
        }

        # User interactions
        customer -> cosmeticStore.customerWebApp.homeComponent "Visits landing page, views featured products"
        customer -> cosmeticStore.customerWebApp.loginComponent "Authenticates with email/password or social login"
        customer -> cosmeticStore.customerWebApp.registerComponent "Creates new account"
        customer -> cosmeticStore.customerWebApp.productDetailComponent "Views product information and adds to cart"
        customer -> cosmeticStore.customerWebApp.cartComponent "Manages shopping cart items"
        customer -> cosmeticStore.customerWebApp.checkoutComponent "Places orders and makes payments"
        customer -> cosmeticStore.customerWebApp.chatComponent "Communicates with customer support"
        
        # Component interactions within the app
        cosmeticStore.customerWebApp.headerComponent -> cosmeticStore.customerWebApp.userContext "Displays user authentication status"
        cosmeticStore.customerWebApp.headerComponent -> cosmeticStore.customerWebApp.cartContext "Shows cart item counter"
        
        cosmeticStore.customerWebApp.loginComponent -> cosmeticStore.customerWebApp.userContext "Updates user authentication state"
        cosmeticStore.customerWebApp.registerComponent -> cosmeticStore.customerWebApp.userContext "Sets user data after registration"
        cosmeticStore.customerWebApp.oauthRedirectComponent -> cosmeticStore.customerWebApp.userContext "Processes OAuth2 login result"
        
        cosmeticStore.customerWebApp.productDetailComponent -> cosmeticStore.customerWebApp.cartContext "Adds products to cart"
        cosmeticStore.customerWebApp.cartComponent -> cosmeticStore.customerWebApp.cartContext "Manages cart state"
        cosmeticStore.customerWebApp.checkoutComponent -> cosmeticStore.customerWebApp.cartContext "Clears cart after successful order"
        
        cosmeticStore.customerWebApp.userContext -> cosmeticStore.customerWebApp.userReducer "Dispatches user actions"
        cosmeticStore.customerWebApp.cartContext -> cosmeticStore.customerWebApp.cartReducer "Dispatches cart actions"
        
        # API interactions
        cosmeticStore.customerWebApp.loginComponent -> cosmeticStore.customerWebApp.apiService "Authenticates user credentials"
        cosmeticStore.customerWebApp.registerComponent -> cosmeticStore.customerWebApp.apiService "Creates new user account"
        cosmeticStore.customerWebApp.productDetailComponent -> cosmeticStore.customerWebApp.apiService "Fetches product details"
        cosmeticStore.customerWebApp.homeComponent -> cosmeticStore.customerWebApp.apiService "Loads featured products"
        cosmeticStore.customerWebApp.cartComponent -> cosmeticStore.customerWebApp.apiService "Syncs cart with backend"
        cosmeticStore.customerWebApp.checkoutComponent -> cosmeticStore.customerWebApp.apiService "Processes order placement"
        cosmeticStore.customerWebApp.ordersComponent -> cosmeticStore.customerWebApp.apiService "Fetches order history"
        cosmeticStore.customerWebApp.profileComponent -> cosmeticStore.customerWebApp.apiService "Updates user profile"
        
        # Backend API interactions
        cosmeticStore.customerWebApp.apiService -> cosmeticStore.apiBackend "Makes authenticated REST API calls" "HTTPS/JWT"
        
        # External service interactions
        cosmeticStore.customerWebApp.chatComponent -> cosmeticStore.customerWebApp.chatService "Manages real-time messaging"
        cosmeticStore.customerWebApp.chatService -> cosmeticStore.customerWebApp.firebaseConfig "Uses Firebase configuration"
        cosmeticStore.customerWebApp.firebaseConfig -> firebaseService "Connects to Firestore for real-time chat" "Firebase SDK"
        
        cosmeticStore.customerWebApp.loginComponent -> googleOAuth "Initiates Google social login" "OAuth2 Redirect"
        cosmeticStore.customerWebApp.loginComponent -> facebookOAuth "Initiates Facebook social login" "OAuth2 Redirect"
        cosmeticStore.customerWebApp.oauthRedirectComponent -> googleOAuth "Receives OAuth2 callback" "OAuth2 Token Exchange"
        cosmeticStore.customerWebApp.oauthRedirectComponent -> facebookOAuth "Receives OAuth2 callback" "OAuth2 Token Exchange"
    }

    views {
        component cosmeticStore.customerWebApp "CustomerWebApp-Components" "Component diagram for the React Customer Web Application" {
            include *
            exclude cosmeticStore.apiBackend
            title "Customer Web Application - Component Diagram"
            description "This diagram shows the internal components of the React Customer Web Application, including UI components, state management, and external service integrations."
            autolayout lr
        }

        styles {
            element "Element" {
                color #000000
                stroke #cccccc
                strokeWidth 1
                shape roundedbox
                fontSize 10
            }
            element "Person" {
                background #08427b
                color #ffffff
                shape person
                fontSize 11
            }
            element "External System" {
                background #999999
                color #ffffff
                fontSize 9
            }
            element "Backend API" {
                background #666666
                color #ffffff
                fontSize 9
            }
            element "UI Component" {
                background #1168bd
                color #ffffff
                fontSize 9
            }
            element "Layout" {
                background #0d5394
                color #ffffff
            }
            element "Page" {
                background #2980b9
                color #ffffff
            }
            element "Auth Component" {
                background #e74c3c
                color #ffffff
            }
            element "Product Component" {
                background #27ae60
                color #ffffff
            }
            element "Commerce Component" {
                background #f39c12
                color #ffffff
            }
            element "User Component" {
                background #9b59b6
                color #ffffff
            }
            element "Communication Component" {
                background #16a085
                color #ffffff
            }
            element "Payment Component" {
                background #d35400
                color #ffffff
            }
            element "Info Component" {
                background #7f8c8d
                color #ffffff
            }
            element "State Management" {
                background #34495e
                color #ffffff
                shape roundedbox
            }
            element "Context" {
                background #2c3e50
                color #ffffff
            }
            element "Reducer" {
                background #34495e
                color #ffffff
            }
            element "API Service" {
                background #8e44ad
                color #ffffff
                shape hexagon
            }
            element "External Service" {
                background #c0392b
                color #ffffff
            }
            element "Configuration" {
                background #d68910
                color #ffffff
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