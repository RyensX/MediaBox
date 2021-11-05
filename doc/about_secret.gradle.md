# 关于secret.gradle文件

- #### 为什么没有上传此文件到Github？

  此文件包括了一些敏感信息，例如安装包签名密码、各种密钥等，因此没有上传到Github上。

- #### 编译时提示缺少此文件怎么办？

  你可以尝试将用到此文件内容的代码全部**<u>注释</u>**。**<u>不建议</u>**对文件内容**<u>随意设置值</u>**，然后进行编译，因为某些功能需要的密钥是唯一的，若随意设置值，可能**<u>仍然会导致编译失败</u>**或其他问题。

- #### 此文件的内容结构是怎样的？

  ```groovy
  def secret = [:]
  
  // ===== in Code =====
  def buildConfigField = [:]
  buildConfigField.UMENG_MESSAGE_SECRET = "..."
  secret.buildConfigField = buildConfigField
  def shieldTextList = [:]
  shieldTextList.SHIELD_TEXT = '{..., ..., ...}'
  secret.shieldTextList = shieldTextList
  // ===== in Code end =====
  
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
  secret.manifestPlaceholders = manifestPlaceholders
  // ===== in Manifest end =====
  
  ext.secret = secret
  
  ```
  
  

