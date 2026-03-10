module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  rules: {
    // Vue 相关规则
    'vue/multi-word-component-names': 'off',
    'vue/no-v-html': 'off',
    'vue/require-default-prop': 'off',
    'vue/html-self-closing': ['error', {
      html: {
        void: 'always',
        normal: 'never',
        component: 'always'
      }
    }],
    'vue/max-attributes-per-line': ['error', {
      singleline: 3,
      multiline: 1
    }],
    'vue/component-tags-order': ['error', {
      order: ['template', 'script', 'style']
    }],
    'vue/no-unused-vars': 'error',
    'vue/no-mutating-props': 'error',
    'vue/require-v-for-key': 'error',
    'vue/valid-v-for': 'error',
    
    // JavaScript 规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
    'prefer-const': 'error',
    'no-var': 'error',
    'eqeqeq': ['error', 'always'],
    'no-eval': 'error',
    'no-implied-eval': 'error',
    
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
    
    // 安全相关
    'no-new-func': 'error',
    'no-script-url': 'error'
  },
  overrides: [
    {
      files: ['**/__tests__/**/*.js', '**/*.test.js'],
      env: {
        jest: true
      }
    }
  ]
}
