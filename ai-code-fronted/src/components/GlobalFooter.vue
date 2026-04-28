<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <!-- 备案信息 -->
      <p class="beian">
        备案号：苏ICP备XXXXX号
      </p>

      <!-- 联系方式：QQ 邮箱，点击可发送邮件 -->
      <p class="contact">
        联系邮箱：
        <a
          href="mailto:2119527099@qq.com"
          target="_blank"
          rel="noopener noreferrer"
          class="email-link"
          title="点击发送邮件到 2119527099@qq.com"
        >
          2119527099@qq.com
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">

</script>

<style scoped>
.footer {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  text-align: center;
  padding: 20px;
  margin-top: 40px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 新增样式，保持简洁 */
.beian,
.contact {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.email-link {
  color: #2d8cf0;
  text-decoration: none;
}

.email-link:hover {
  text-decoration: underline;
}
</style>
