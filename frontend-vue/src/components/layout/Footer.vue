<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>房屋租赁平台致力于为用户提供最优质的租房体验，让找房更轻松、更安全。</p>
        </div>
        
        <div class="footer-section">
          <h4>快速链接</h4>
          <ul>
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/search">找房</router-link></li>
            <li><router-link to="/publish">发布房源</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>联系方式</h4>
          <ul>
            <li>客服热线：400-123-4567</li>
            <li>工作时间：9:00 - 18:00</li>
            <li>邮箱：support@houserental.com</li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>关注我们</h4>
          <div class="social-links">
            <a href="#" class="social-link">
              <el-icon><ChatDotRound /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><Message /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><VideoCamera /></el-icon>
            </a>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2024 房屋租赁平台 版权所有 | 京ICP备12345678号</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section {
  h4 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 15px;
    color: white;
  }
  
  p {
    font-size: 14px;
    line-height: 1.6;
    color: #bdc3c7;
  }
  
  ul {
    list-style: none;
    
    li {
      margin-bottom: 10px;
      
      a {
        color: #bdc3c7;
        text-decoration: none;
        transition: $transition-base;
        
        &:hover {
          color: $primary-color;
        }
      }
    }
  }
}

.social-links {
  display: flex;
  gap: 15px;
  
  .social-link {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ecf0f1;
    transition: $transition-base;
    
    &:hover {
      background: $primary-color;
      transform: translateY(-3px);
    }
    
    .el-icon {
      font-size: 20px;
    }
  }
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 20px;
  text-align: center;
  
  p {
    font-size: 13px;
    color: #95a5a6;
  }
}
</style>
