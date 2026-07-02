const AUTH_USER_KEY = 'gov-service-auth-user'
const ACCOUNT_KEY = 'gov-service-account-list'

const defaultAccounts = [
  {
    username: 'admin',
    password: '123456',
    name: '系统管理员',
    role: '系统管理员',
    department: '行政审批局',
    phone: '13800000000',
    createdAt: '2026-07-02 09:00:00',
  },
  {
    username: 'user',
    password: '123456',
    name: '办事用户',
    role: '普通用户',
    department: '个人办事端',
    phone: '13900000000',
    createdAt: '2026-07-02 09:00:00',
  },
]

function safeParse(value, fallback) {
  try {
    return JSON.parse(value) || fallback
  } catch (error) {
    return fallback
  }
}

export function getAccounts() {
  const storedAccounts = safeParse(localStorage.getItem(ACCOUNT_KEY), [])
  const mergedAccounts = [...defaultAccounts]

  storedAccounts.forEach((account) => {
    if (!mergedAccounts.some((item) => item.username === account.username)) {
      mergedAccounts.push(account)
    }
  })

  localStorage.setItem(ACCOUNT_KEY, JSON.stringify(mergedAccounts))
  return mergedAccounts
}

export function saveAccounts(accounts) {
  localStorage.setItem(ACCOUNT_KEY, JSON.stringify(accounts))
}

export function toSessionUser(account) {
  return {
    username: account.username,
    name: account.name,
    role: account.role,
    department: account.department,
    phone: account.phone,
    loginAt: new Date().toLocaleString('zh-CN', { hour12: false }),
  }
}

export function getCurrentUser() {
  return safeParse(localStorage.getItem(AUTH_USER_KEY), null)
}

export function isLoggedIn() {
  return Boolean(getCurrentUser())
}

export function loginWithPassword(username, password) {
  const normalizedUsername = username.trim()
  const account = getAccounts().find((item) => item.username === normalizedUsername)

  if (!account || account.password !== password) {
    throw new Error('账号或密码错误')
  }

  const sessionUser = toSessionUser(account)
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify(sessionUser))
  return sessionUser
}

export function registerAccount(form) {
  const accounts = getAccounts()
  const normalizedUsername = form.username.trim()

  if (accounts.some((item) => item.username === normalizedUsername)) {
    throw new Error('该账号已存在')
  }

  const nextAccount = {
    username: normalizedUsername,
    password: form.password,
    name: form.name.trim(),
    role: form.role,
    department: form.department.trim(),
    phone: form.phone.trim(),
    createdAt: new Date().toLocaleString('zh-CN', { hour12: false }),
  }

  accounts.push(nextAccount)
  saveAccounts(accounts)

  const sessionUser = toSessionUser(nextAccount)
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify(sessionUser))
  return sessionUser
}

export function logout() {
  localStorage.removeItem(AUTH_USER_KEY)
}
