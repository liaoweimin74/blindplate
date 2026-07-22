import { createI18n } from 'vue-i18n'
import zhCN from '@/locales/zh-CN.json'
import en from '@/locales/en.json'

const DEFAULT_LOCALE = 'zh-CN'

function getLocale(): string {
  const saved = localStorage.getItem('language')
  if (saved && ['zh-CN', 'en'].includes(saved)) {
    return saved
  }
  return DEFAULT_LOCALE
}

const i18n = createI18n({
  legacy: false,
  locale: getLocale(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    en
  }
})

export function setLocale(locale: string) {
  i18n.global.locale.value = locale as any
  localStorage.setItem('language', locale)
}

export default i18n