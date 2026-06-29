# Client
src/app/
├── app.component.ts             # Main container
├── app.component.html           # Template with router-outlet
├── app.config.ts                # App configuration
├── app.routes.ts                # Main routing
│
├── core/                        # CORE - singleton services, guards
│   ├── guards/                  # Route guards
│   │   ├── auth.guard.ts        # Checks if user is authenticated
│   │   └── role.guard.ts        # Checks if user has required role
│   ├── interceptors/            # HTTP interceptors
│   │   └── auth.interceptor.ts  # Adds auth token to requests
│   └── services/                # Core services
│       ├── auth.service.ts      # Login/Logout/Register
│       └── user.service.ts      # Current user data
│
├── shared/                      # SHARED - reused everywhere
│   ├── components/              # Shared components
│   │   ├── navbar/              # Top navigation (visible to all)
│   │   ├── sidebar/             # Sidebar (changes by role)
│   │   └── footer/              # Footer (if needed)
│   └── models/                  # Data models (interfaces)
│       ├── user.model.ts        # User model
│       └── role.enum.ts         # Role enum
│
├── features/                    # FEATURES - logical modules
│   ├── auth/                    # Authentication (public)
│   │   ├── login/               # Login page
│   │   │   ├── login.component.ts
│   │   │   └── login.component.html
│   │   └── register/            # Registration page
│   │       ├── register.component.ts
│   │       └── register.component.html
│   │
│   ├── admin/                   # Admin panel (ADMIN only)
│   │   ├── admin-dashboard/     # Admin dashboard
│   │   ├── users-management/    # User management
│   │   └── settings/            # System settings
│   │
│   ├── hr/                      # HR panel (HR only)
│   │   ├── hr-dashboard/        # HR dashboard
│   │   ├── candidates/          # Candidate management
│   │   └── vacancies/           # Vacancy management
│   │
│   └── member/                  # Member (MEMBER only)
│       ├── member-dashboard/    # Member dashboard
│       ├── profile/             # Personal profile
│       └── tasks/               # My tasks
│
└── shared/components/           # More shared components
├── dashboard-wrapper/       # Dashboard wrapper
└── not-found/               # 404 page
