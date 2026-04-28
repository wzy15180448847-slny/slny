# 房源卡片ID重复问题修复计划

## 一、问题分析

### 1.1 问题描述
前端页面中所有房源卡片显示的房源ID均为同一个值，导致用户点击任何房源卡片时都会导航至相同的URL。

### 1.2 初步排查结果
- 后端API请求返回的房源数据包含不同且不重复的房源ID ✓
- 数据库中存储的房源ID不存在重复情况 ✓
- 问题出现在前端数据处理或渲染环节

### 1.3 代码分析发现的问题

#### 问题1：store/house.js 响应解构错误
```javascript
// 错误代码
const { data } = await getHouseList({...})

// 正确代码
const data = await getHouseList({...})
```
由于 `request.js` 拦截器已经直接返回 `res.data`，所以不需要再次解构 `{ data }`。这个错误会导致 `data` 为 `undefined` 或结构错误。

#### 问题2：store/house.js fetchHouseDetail 同样存在解构问题
```javascript
// 错误代码  
const { data } = await getHouseDetail(id)

// 正确代码
const data = await getHouseDetail(id)
```

#### 问题3：store/house.js searchHouses 同样存在解构问题
```javascript
// 错误代码
const { data } = await searchHouses({...})

// 正确代码
const data = await searchHouses({...})
```

## 二、修复计划

### 2.1 需要修改的文件

| 文件路径 | 修改内容 | 优先级 |
| :--- | :--- | :--- |
| `src/store/house.js` | 修复响应解构问题 | 高 |
| `src/components/house/HouseCard.vue` | 验证ID绑定逻辑 | 高 |
| `src/views/house/search.vue` | 验证数据处理流程 | 中 |
| `src/views/home/index.vue` | 验证数据处理流程 | 中 |

### 2.2 修复步骤

**步骤1：修复 store/house.js**
- 将 `fetchHouseList` 中的 `const { data } = await getHouseList(...)` 改为 `const data = await getHouseList(...)`
- 将 `fetchHouseDetail` 中的 `const { data } = await getHouseDetail(id)` 改为 `const data = await getHouseDetail(id)`
- 将 `searchHouses` 中的 `const { data } = await searchHouses(...)` 改为 `const data = await searchHouses(...)`

**步骤2：验证 HouseCard 组件**
- 确认组件正确接收 `house` prop
- 确认点击事件中正确使用 `props.house.id`
- 确认 `:key="house.id"` 正确绑定

**步骤3：验证数据处理流程**
- 确认 `search.vue` 和 `home/index.vue` 中数据正确赋值给 `houseList`
- 确认 v-for 循环正确绑定

### 2.3 测试验证

修复完成后，需要进行以下测试：
1. 首页房源列表 - 确认每个卡片显示不同的房源ID
2. 搜索页面房源列表 - 确认每个卡片显示不同的房源ID
3. 点击测试 - 确认点击不同卡片导航到不同的URL
4. 响应式测试 - 确认在不同屏幕尺寸下正常工作

## 三、风险评估

| 风险项 | 风险等级 | 应对措施 |
| :--- | :--- | :--- |
| 修复后数据无法加载 | 高 | 修复前备份代码，修复后进行完整测试 |
| 其他功能受影响 | 中 | 仅修改响应解构部分，不改动其他逻辑 |
| 数据类型不匹配 | 低 | 确认API返回数据结构与前端期望一致 |

## 四、实施时间线

| 阶段 | 时间估计 |
| :--- | :--- |
| 代码修复 | 30分钟 |
| 测试验证 | 30分钟 |
| 文档更新 | 15分钟 |

---

**计划状态**: 待批准

请审查以上计划，如有问题请指出。批准后将立即执行修复。