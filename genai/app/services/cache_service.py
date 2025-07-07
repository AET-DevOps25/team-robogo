import json
import hashlib
import logging
from typing import Optional, Any, Union
import redis.asyncio as redis
from app.core.config import REDIS_HOST, REDIS_PORT, REDIS_DB, REDIS_PASSWORD, REDIS_TTL

logger = logging.getLogger(__name__)

class CacheService:
    """Redis cache service class"""
    
    def __init__(self):
        self.redis: Optional[redis.Redis] = None
        self.is_connected = False
    
    async def connect(self):
        """Connect to Redis"""
        try:
            if REDIS_PASSWORD:
                self.redis = redis.Redis(
                    host=REDIS_HOST,
                    port=REDIS_PORT,
                    db=REDIS_DB,
                    password=REDIS_PASSWORD,
                    decode_responses=True
                )
            else:
                self.redis = redis.Redis(
                    host=REDIS_HOST,
                    port=REDIS_PORT,
                    db=REDIS_DB,
                    decode_responses=True
                )
            
            await self.redis.ping()
            self.is_connected = True
            logger.info("Successfully connected to Redis")
        except Exception as e:
            logger.error(f"Failed to connect to Redis: {e}")
            self.is_connected = False
    
    async def disconnect(self):
        """Disconnect from Redis"""
        if self.redis:
            await self.redis.close()
            self.is_connected = False
            logger.info("Disconnected from Redis")
    
    def _generate_cache_key(self, text: str, service: str) -> str:
        """Generate cache key"""
        # Create a unique identifier containing text and service
        content = f"{text}:{service}"
        # Use MD5 hash to generate a short unique key
        hash_key = hashlib.md5(content.encode()).hexdigest()
        return f"ai_suggestion:{hash_key}"
    
    async def get_cached_suggestion(self, text: str, service: str) -> Optional[str]:
        """Get cached suggestion"""
        if not self.is_connected:
            await self.connect()
        
        if not self.is_connected or not self.redis:
            logger.warning("Redis not connected, skipping cache lookup")
            return None
        
        try:
            cache_key = self._generate_cache_key(text, service)
            cached_data = await self.redis.get(cache_key)
            
            if cached_data:
                logger.info(f"Cache hit for key: {cache_key}")
                return cached_data
            else:
                logger.info(f"Cache miss for key: {cache_key}")
                return None
        except Exception as e:
            logger.error(f"Error retrieving from cache: {e}")
            return None
    
    async def cache_suggestion(self, text: str, service: str, suggestion: str) -> bool:
        """Cache suggestion"""
        if not self.is_connected:
            await self.connect()
        
        if not self.is_connected or not self.redis:
            logger.warning("Redis not connected, skipping cache storage")
            return False
        
        try:
            cache_key = self._generate_cache_key(text, service)
            await self.redis.setex(cache_key, REDIS_TTL, suggestion)
            logger.info(f"Cached suggestion for key: {cache_key}")
            return True
        except Exception as e:
            logger.error(f"Error caching suggestion: {e}")
            return False
    
    async def clear_cache(self) -> bool:
        """Clear all AI suggestion cache"""
        if not self.is_connected:
            await self.connect()
        
        if not self.is_connected or not self.redis:
            logger.warning("Redis not connected, cannot clear cache")
            return False
        
        try:
            # Find all AI suggestion cache keys
            keys = await self.redis.keys("ai_suggestion:*")
            if keys:
                await self.redis.delete(*keys)
                logger.info(f"Cleared {len(keys)} cached suggestions")
            return True
        except Exception as e:
            logger.error(f"Error clearing cache: {e}")
            return False
    
    async def get_cache_stats(self) -> dict:
        """Get cache statistics"""
        if not self.is_connected:
            await self.connect()
        
        if not self.is_connected or not self.redis:
            return {"connected": False, "total_keys": 0}
        
        try:
            keys = await self.redis.keys("ai_suggestion:*")
            info = await self.redis.info()
            return {
                "connected": True,
                "total_keys": len(keys),
                "redis_version": info.get("redis_version", "unknown"),
                "used_memory": info.get("used_memory_human", "unknown"),
                "connected_clients": info.get("connected_clients", 0)
            }
        except Exception as e:
            logger.error(f"Error getting cache stats: {e}")
            return {"connected": False, "error": str(e)}

# Global cache service instance
cache_service = CacheService() 