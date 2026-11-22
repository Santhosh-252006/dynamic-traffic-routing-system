from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import User
from app.schemas import RegisterRequest, LoginRequest, AuthResponse, UserDto
from app.security import verify_password, get_password_hash, create_access_token, get_current_user

router = APIRouter(prefix="/auth", tags=["Authentication API"])

@router.post("/register", response_model=AuthResponse)
def register(request: RegisterRequest, db: Session = Depends(get_db)):
    existing = db.query(User).filter(User.email == request.email).first()
    if existing:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email address already in use!"
        )

    user_role = "ROLE_ADMIN" if request.role and request.role.upper() == "ROLE_ADMIN" else "ROLE_USER"
    user = User(
        name=request.name,
        email=request.email,
        hashed_password=get_password_hash(request.password),
        role=user_role
    )
    db.add(user)
    db.commit()
    db.refresh(user)

    token = create_access_token(data={"sub": str(user.id), "email": user.email, "role": user.role})
    return AuthResponse(
        accessToken=token,
        tokenType="Bearer",
        user=UserDto(id=user.id, name=user.name, email=user.email, role=user.role)
    )

@router.post("/login", response_model=AuthResponse)
def login(request: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == request.email).first()
    if not user or not verify_password(request.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid email or password credentials"
        )

    token = create_access_token(data={"sub": str(user.id), "email": user.email, "role": user.role})
    return AuthResponse(
        accessToken=token,
        tokenType="Bearer",
        user=UserDto(id=user.id, name=user.name, email=user.email, role=user.role)
    )

@router.get("/me", response_model=UserDto)
def get_me(current_user: User = Depends(get_current_user)):
    return UserDto(
        id=current_user.id,
        name=current_user.name,
        email=current_user.email,
        role=current_user.role
    )
