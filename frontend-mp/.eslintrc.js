module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  globals: {
    // 微信小程序全局变量
    wx: 'readonly',
    App: 'readonly',
    Page: 'readonly',
    Component: 'readonly',
    Behavior: 'readonly',
    getApp: 'readonly',
    getCurrentPages: 'readonly'
  },
  extends: [
    'eslint:recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  rules: {
    // 代码质量
    'no-console': 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
    'prefer-const': 'error',
    'no-var': 'error',
    'eqeqeq': ['error', 'always'],
    
    // 安全相关
    'no-eval': 'error',
    'no-implied-eval': 'error',
    'no-new-func': 'error',
    'no-script-url': 'error',
    
    // 代码风格
    'indent': ['error', 2],
    'quotes': ['error', 'single'],
    'semi': ['error', 'never'],
    'comma-dangle': ['error', 'never'],
    'object-curly-spacing': ['error', 'always'],
    'array-bracket-spacing': ['error', 'never'],
    'arrow-spacing': 'error',
    'key-spacing': 'error',
    'keyword-spacing': 'error',
    'space-before-blocks': 'error',
    'space-infix-ops': 'error',
    
    // 最佳实践
    'no-empty-function': 'warn',
    'no-magic-numbers': ['warn', { 
      ignore: [0, 1, -1, 200, 401, 403, 404, 500],
      ignoreArrayIndexes: true 
    }],
    'no-param-reassign': 'warn'
  }
}
