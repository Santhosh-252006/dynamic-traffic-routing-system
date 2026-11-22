import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "Dynamic Traffic Routing System"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api"
    
    JWT_SECRET: str = os.getenv(
        "JWT_SECRET", 
        "9a2f8c4e6b1d3a5f7e9c2b4a6d8f1e3c5b7a9d2f4e6b8c1a3f5e7d9b2c4a6f8e"
    )
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24  # 24 hours
    
    DATABASE_URL: str = os.getenv("DATABASE_URL", "sqlite:///./traffic.db")
    
    CORS_ORIGINS: list[str] = ["*"]

settings = Settings()
