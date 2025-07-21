# API Access Guide - Using Postman with GitHub Token Authentication

This guide explains how to access the secured API endpoints using Postman with GitHub Personal Access Tokens.

## Overview

The API uses **GitHub Personal Access Tokens** for authentication. Users must obtain a token from GitHub and include it in their HTTP requests to access protected resources.

## Authentication Architecture

- **Authentication Method**: GitHub Personal Access Token
- **Authorization Header**: `Authorization: Bearer <github-token>`
- **User Roles**: 
  - `ROLE_USER` - Granted to all authenticated GitHub users
  - `ROLE_ADMIN` - Granted to specific GitHub usernames configured in the system

## Step 1: Obtain GitHub Personal Access Token

### Creating a GitHub Token

1. **Log in to GitHub** and go to [Settings > Developer settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens)

2. **Click "Generate new token"** → "Generate new token (classic)"

3. **Configure your token**:
   - **Note**: `Spring Boot API Access` (or any descriptive name)
   - **Expiration**: Choose appropriate expiration (30 days, 90 days, etc.)
   - **Scopes**: Select the minimum required scopes:
     - ✅ `read:user` - Read access to user profile information
     - ✅ `user:email` - Access to user email addresses

4. **Generate and copy the token** - ⚠️ **Important**: Save this token immediately as you won't be able to see it again!

### Example Token
```
ghp_1234567890abcdef1234567890abcdef12345678
```

## Step 2: Configure Postman

### Setting up Environment Variables (Recommended)

1. **Create a new Environment** in Postman:
   - Environment Name: `Spring Boot GitHub Auth`

2. **Add Variables**:
   - Variable: `github_token`
   - Initial Value: `your-github-token-here`
   - Current Value: `your-github-token-here`
   - Variable: `base_url`
   - Initial Value: `http://localhost:8080`
   - Current Value: `http://localhost:8080`

### Manual Header Setup (Alternative)

If not using environment variables, you'll manually add the authorization header to each request.

## Step 3: API Endpoints and Access Levels

### 🌐 Public Endpoints (No Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Home page |
| GET | `/index.html` | API testing interface |
| GET | `/auth/login-instructions` | Authentication instructions |

### 🔐 User Endpoints (Requires ROLE_USER)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/user` | Get current user information |
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### 👑 Admin Endpoints (Requires ROLE_ADMIN)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/**` | Admin-only endpoints |

## Step 4: Making Requests in Postman

### Example 1: Get Current User Information

**Request Configuration:**
```http
GET {{base_url}}/auth/user
Authorization: Bearer {{github_token}}
Content-Type: application/json
```

**Postman Setup:**
1. Method: `GET`
2. URL: `{{base_url}}/auth/user`
3. Headers:
   - `Authorization`: `Bearer {{github_token}}`
   - `Content-Type`: `application/json`

**Expected Response:**
```json
{
  "login": "your-github-username",
  "name": "Your Full Name",
  "email": "your-email@example.com",
  "avatarUrl": "https://avatars.githubusercontent.com/u/12345?v=4"
}
```

### Example 2: List All Users

**Request Configuration:**
```http
GET {{base_url}}/api/users
Authorization: Bearer {{github_token}}
Content-Type: application/json
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  {
    "id": 2,
    "name": "Jane Smith", 
    "email": "jane@example.com"
  }
]
```

### Example 3: Create New User

**Request Configuration:**
```http
POST {{base_url}}/api/users
Authorization: Bearer {{github_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Alice Johnson",
  "email": "alice@example.com"
}
```

**Expected Response:**
```json
{
  "id": 3,
  "name": "Alice Johnson",
  "email": "alice@example.com"
}
```

### Example 4: Update User

**Request Configuration:**
```http
PUT {{base_url}}/api/users/1
Authorization: Bearer {{github_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
```

### Example 5: Delete User

**Request Configuration:**
```http
DELETE {{base_url}}/api/users/1
Authorization: Bearer {{github_token}}
```

**Expected Response:**
```
HTTP 200 OK
(No content)
```

## Step 5: Testing Different Scenarios

### ✅ Valid Token Test

1. Use a valid GitHub token
2. Expect successful responses (200, 201, etc.)
3. Access to user and admin endpoints based on your role

### ❌ Invalid Token Test

1. Use an invalid or expired token
2. Expect `401 Unauthorized` responses
3. Error message: `"Access Denied"`

### ❌ No Token Test

1. Send requests without Authorization header
2. Expect `401 Unauthorized` responses
3. Error message: `"Access Denied"`

### 🔒 Role-Based Access Test

1. **Regular User**: Can access `/api/users/**` endpoints
2. **Admin User**: Can access both `/api/users/**` and `/api/admin/**` endpoints
3. **Non-Admin trying Admin endpoint**: Expect `403 Forbidden`

## Step 6: Postman Collection Example

Here's a complete Postman collection JSON you can import:

```json
{
  "info": {
    "name": "Spring Boot GitHub Auth API",
    "description": "API collection for GitHub token-based authentication"
  },
  "auth": {
    "type": "bearer",
    "bearer": [
      {
        "key": "token",
        "value": "{{github_token}}",
        "type": "string"
      }
    ]
  },
  "item": [
    {
      "name": "Auth - Get User Info",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/auth/user",
          "host": ["{{base_url}}"],
          "path": ["auth", "user"]
        }
      }
    },
    {
      "name": "Users - List All",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/api/users",
          "host": ["{{base_url}}"],
          "path": ["api", "users"]
        }
      }
    },
    {
      "name": "Users - Create New",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Test User\",\n  \"email\": \"test@example.com\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/users",
          "host": ["{{base_url}}"],
          "path": ["api", "users"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    },
    {
      "key": "github_token",
      "value": "your-github-token-here"
    }
  ]
}
```

## Troubleshooting

### Common Issues and Solutions

1. **401 Unauthorized Error**
   - ✅ Verify your GitHub token is valid and not expired
   - ✅ Check the Authorization header format: `Bearer <token>`
   - ✅ Ensure the token has correct scopes (`read:user`)

2. **403 Forbidden Error**
   - ✅ Check if you have the required role (USER/ADMIN) for the endpoint
   - ✅ Verify admin users are configured correctly in `GitHubTokenAuthenticationFilter`

3. **Token Validation Failures**
   - ✅ Test the token directly with GitHub API: `curl -H "Authorization: Bearer <token>" https://api.github.com/user`
   - ✅ Check application logs for specific error messages

4. **CORS Issues**
   - ✅ Ensure CORS is properly configured in SecurityConfig
   - ✅ Check if Origin header is being sent correctly

### Debugging Commands

**Test GitHub Token Manually:**
```bash
curl -H "Authorization: Bearer your-token-here" https://api.github.com/user
```

**Check Application Logs:**
```bash
# Look for authentication-related log messages
tail -f application.log | grep "GitHub token"
```

## Admin User Configuration

To configure admin users, update the `adminUsers` list in `GitHubTokenAuthenticationFilter.java`:

```java
private final List<String> adminUsers = Arrays.asList(
    "your-github-username",    // Replace with actual GitHub username
    "admin-user2",             // Add more admin usernames as needed
    "another-admin-user"
);
```

## Security Best Practices

1. **Token Management**:
   - Use environment variables for tokens in Postman
   - Set appropriate token expiration dates
   - Rotate tokens regularly

2. **Scope Limitation**:
   - Only grant minimum required scopes
   - Avoid using tokens with admin scopes

3. **HTTPS in Production**:
   - Always use HTTPS in production environments
   - Never send tokens over unencrypted connections

4. **Token Storage**:
   - Store tokens securely
   - Never commit tokens to version control

## Example Complete Workflow

1. **Generate GitHub Token** with `read:user` scope
2. **Start Spring Boot Application**: `mvn spring-boot:run`
3. **Open Postman** and create new request
4. **Set Authorization**: Bearer Token with your GitHub token
5. **Test Public Endpoint**: GET `http://localhost:8080/auth/login-instructions`
6. **Test User Endpoint**: GET `http://localhost:8080/auth/user`
7. **Test API Endpoints**: GET `http://localhost:8080/api/users`
8. **Create/Update/Delete** resources as needed

---

## 📞 Support

If you encounter issues:
1. Check the application logs
2. Verify your GitHub token with GitHub's API directly
3. Ensure the Spring Boot application is running on the correct port
4. Review the security configuration in `SecurityConfig.java`
