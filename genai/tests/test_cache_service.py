import pytest
from unittest.mock import AsyncMock, patch, MagicMock
from app.services.cache_service import CacheService


class TestCacheService:
    @pytest.fixture
    def cache_service(self):
        """创建缓存服务实例"""
        return CacheService()

    @pytest.fixture
    def mock_redis(self):
        """创建模拟Redis连接"""
        mock_redis = AsyncMock()
        mock_redis.ping.return_value = "PONG"
        mock_redis.get.return_value = None
        mock_redis.setex.return_value = True
        mock_redis.keys.return_value = []
        mock_redis.delete.return_value = 1
        mock_redis.info.return_value = {
            "redis_version": "7.0.0",
            "used_memory_human": "1.2M",
            "connected_clients": 1
        }
        return mock_redis

    def test_generate_cache_key(self, cache_service):
        """测试缓存键生成"""
        key = cache_service._generate_cache_key("test text", "openai")
        assert key.startswith("ai_suggestion:")
        assert len(key) > 20  # 确保包含哈希值
        
        # 相同输入应该生成相同的键
        key2 = cache_service._generate_cache_key("test text", "openai")
        assert key == key2
        
        # 不同输入应该生成不同的键
        key3 = cache_service._generate_cache_key("different text", "openai")
        assert key != key3

    @pytest.mark.asyncio
    async def test_connect_success(self, cache_service, mock_redis):
        """测试成功连接到Redis"""
        with patch('redis.asyncio.Redis', return_value=mock_redis):
            await cache_service.connect()
            assert cache_service.is_connected is True
            assert cache_service.redis == mock_redis

    @pytest.mark.asyncio
    async def test_connect_failure(self, cache_service):
        """测试连接Redis失败"""
        with patch('redis.asyncio.Redis', side_effect=Exception("Connection failed")):
            await cache_service.connect()
            assert cache_service.is_connected is False

    @pytest.mark.asyncio
    async def test_disconnect(self, cache_service, mock_redis):
        """测试断开Redis连接"""
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        await cache_service.disconnect()
        assert cache_service.is_connected is False
        mock_redis.close.assert_called_once()

    @pytest.mark.asyncio
    async def test_get_cached_suggestion_hit(self, cache_service, mock_redis):
        """测试缓存命中"""
        mock_redis.get.return_value = "缓存的建议"
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        result = await cache_service.get_cached_suggestion("test text", "openai")
        assert result == "缓存的建议"

    @pytest.mark.asyncio
    async def test_get_cached_suggestion_miss(self, cache_service, mock_redis):
        """测试缓存未命中"""
        mock_redis.get.return_value = None
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        result = await cache_service.get_cached_suggestion("test text", "openai")
        assert result is None

    @pytest.mark.asyncio
    async def test_get_cached_suggestion_not_connected(self, cache_service):
        """测试Redis未连接时获取缓存"""
        cache_service.is_connected = False
        
        with patch.object(cache_service, 'connect', return_value=None):
            result = await cache_service.get_cached_suggestion("test text", "openai")
            assert result is None

    @pytest.mark.asyncio
    async def test_cache_suggestion_success(self, cache_service, mock_redis):
        """测试成功缓存建议"""
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        with patch('app.core.config.REDIS_TTL', 3600):
            result = await cache_service.cache_suggestion("test text", "openai", "AI建议")
            assert result is True
            mock_redis.setex.assert_called_once()

    @pytest.mark.asyncio
    async def test_cache_suggestion_not_connected(self, cache_service):
        """测试Redis未连接时缓存建议"""
        cache_service.is_connected = False
        
        with patch.object(cache_service, 'connect', return_value=None):
            result = await cache_service.cache_suggestion("test text", "openai", "AI建议")
            assert result is False

    @pytest.mark.asyncio
    async def test_cache_suggestion_error(self, cache_service, mock_redis):
        """测试缓存建议时出错"""
        mock_redis.setex.side_effect = Exception("Redis error")
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        result = await cache_service.cache_suggestion("test text", "openai", "AI建议")
        assert result is False

    @pytest.mark.asyncio
    async def test_clear_cache_success(self, cache_service, mock_redis):
        """测试成功清除缓存"""
        mock_redis.keys.return_value = ["ai_suggestion:key1", "ai_suggestion:key2"]
        mock_redis.delete.return_value = 2
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        result = await cache_service.clear_cache()
        assert result is True
        mock_redis.keys.assert_called_once_with("ai_suggestion:*")
        mock_redis.delete.assert_called_once()

    @pytest.mark.asyncio
    async def test_clear_cache_no_keys(self, cache_service, mock_redis):
        """测试清除空缓存"""
        mock_redis.keys.return_value = []
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        result = await cache_service.clear_cache()
        assert result is True
        mock_redis.delete.assert_not_called()

    @pytest.mark.asyncio
    async def test_get_cache_stats_success(self, cache_service, mock_redis):
        """测试成功获取缓存统计信息"""
        mock_redis.keys.return_value = ["ai_suggestion:key1", "ai_suggestion:key2"]
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        stats = await cache_service.get_cache_stats()
        assert stats["connected"] is True
        assert stats["total_keys"] == 2
        assert stats["redis_version"] == "7.0.0"
        assert stats["used_memory"] == "1.2M"
        assert stats["connected_clients"] == 1

    @pytest.mark.asyncio
    async def test_get_cache_stats_not_connected(self, cache_service):
        """测试Redis未连接时获取统计信息"""
        cache_service.is_connected = False
        
        with patch.object(cache_service, 'connect', return_value=None):
            stats = await cache_service.get_cache_stats()
            assert stats["connected"] is False
            assert stats["total_keys"] == 0

    @pytest.mark.asyncio
    async def test_get_cache_stats_error(self, cache_service, mock_redis):
        """测试获取统计信息时出错"""
        mock_redis.keys.side_effect = Exception("Redis error")
        cache_service.redis = mock_redis
        cache_service.is_connected = True
        
        stats = await cache_service.get_cache_stats()
        assert stats["connected"] is False
        assert "error" in stats 