# 关于secret.gradle文件

- #### 为什么没有上传此文件到Github？
  
  此文件包括了一些敏感信息，例如安装包签名密码、各种密钥等，因此没有上传到Github上。

- #### 此文件的内容结构是怎样的？
  
  ```groovy
  def secret = [:]
  
  // ===== sign key =====
  def sign = [:]
  sign.RELEASE_KEY_ALIAS = "..."
  sign.RELEASE_KEY_PASSWORD = "..."
  sign.RELEASE_STORE_PASSWORD = "..."
  secret.sign = sign
  // ===== sign key end =====
  
  // ===== in Manifest =====
  def manifestPlaceholders = [:]
  manifestPlaceholders.UMENG_APPKEY_VALUE = "..."
  manifestPlaceholders.UMENG_MESSAGE_SECRET = "..."
  secret.manifestPlaceholders = manifestPlaceholders
  // ===== in Manifest end =====
  
  ext.secret = secret
  ```
