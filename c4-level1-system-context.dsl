workspace "Cosmetic Store - System Context" "Level 1: System Context Diagram showing the overall system boundary and external actors" {

    !identifiers hierarchical

    model {
        # Primary Actors
        customer = person "Customer" "End users who browse and purchase cosmetic products, use real-time chat support, and manage their orders"
        admin = person "Administrator" "Store administrators who manage products, process orders, handle customer support, and access analytics"
        anonymousUser = person "Anonymous User" "Website visitors who can browse products and register for accounts"
        
        # External Systems (Third-party services and integrations)
        googleOAuth = softwareSystem "Google OAuth2" "Google's authentication service for social login functionality" {
            tags "External System"
        }
        
        facebookOAuth = softwareSystem "Facebook OAuth2" "Facebook's authentication service for social login functionality" {
            tags "External System"
        }
        
        momoPayment = softwareSystem "MoMo Payment Gateway" "Vietnamese e-wallet payment service for processing customer transactions" {
            tags "External System,Payment"
        }
        
        firebaseService = softwareSystem "Firebase" "Google's backend-as-a-service providing real-time chat messaging and configuration management" {
            tags "External System,RealTime"
        }
        
        cloudinaryService = softwareSystem "Cloudinary" "Cloud-based image and video management service with CDN for product images and optimization" {
            tags "External System,Media"
        }
        
        emailService = softwareSystem "Email Service (SMTP)" "Email delivery service for password reset notifications and order confirmations" {
            tags "External System,Communication"
        }
        
        # Main Software System
        cosmeticStore = softwareSystem "BFYStore - Cosmetic Store Platform" "Complete e-commerce solution for beauty products with dual frontend architecture and real-time customer support" {
            tags "Main System"
        }

        # Primary User Interactions
        customer -> cosmeticStore "Browses products, manages shopping cart, places orders, tracks deliveries, uses real-time chat support"
        admin -> cosmeticStore "Manages product catalog, processes orders, handles customer support chat, accesses business analytics and reports"
        anonymousUser -> cosmeticStore "Browses product catalog, registers for new account, views product details and categories"
        
        # External System Integrations
        cosmeticStore -> googleOAuth "Authenticates users via Google social login, retrieves user profile information" "HTTPS/OAuth2"
        cosmeticStore -> facebookOAuth "Authenticates users via Facebook social login, retrieves user profile information" "HTTPS/OAuth2"
        cosmeticStore -> momoPayment "Processes payment transactions, handles payment callbacks, manages payment sessions" "HTTPS/REST API"
        cosmeticStore -> firebaseService "Manages real-time chat messaging, stores chat configuration, handles real-time notifications" "HTTPS/Firebase SDK"
        cosmeticStore -> cloudinaryService "Uploads and manages product images, delivers optimized images via CDN, handles image transformations" "HTTPS/REST API"
        cosmeticStore -> emailService "Sends password reset emails, order confirmations, and system notifications" "SMTP"
        
        # Indirect User Interactions with External Systems
        customer -> googleOAuth "Initiates social login flow" "HTTPS/OAuth2"
        customer -> facebookOAuth "Initiates social login flow" "HTTPS/OAuth2"
        customer -> momoPayment "Completes payment transactions in MoMo app/web interface" "HTTPS/Mobile App"
    }

    views {
        systemContext cosmeticStore "CosmeticStore-SystemContext" "System Context diagram for BFYStore Cosmetic E-commerce Platform" {
            include *
            title "BFYStore - System Context Diagram"
            description "This diagram shows the overall system context of the Cosmetic Store platform, including all external actors and third-party systems it integrates with."
            autolayout lr
        }

        styles {
            element "Element" {
                color #000000
                stroke #cccccc
                strokeWidth 2
                shape roundedbox
            }
            element "Person" {
                background #08427b
                color #ffffff
                shape person
                fontSize 12
            }
            element "Main System" {
                background #1168bd
                color #ffffff
                fontSize 14
                strokeWidth 3
            }
            element "External System" {
                background #999999
                color #ffffff
                fontSize 11
            }
            element "Payment" {
                background #d73027
                color #ffffff
            }
            element "RealTime" {
                background #2e8b57
                color #ffffff
            }
            element "Media" {
                background #ff8c00
                color #ffffff
            }
            element "Communication" {
                background #4682b4
                color #ffffff
            }
            relationship "Relationship" {
                thickness 2
                color #707070
                fontSize 10
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}