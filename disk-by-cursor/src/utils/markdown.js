const CODE_BLOCK_TOKEN_PREFIX = '@@CODE_BLOCK_'
const INLINE_CODE_TOKEN_PREFIX = '@@INLINE_CODE_'

function escapeHtml(value = '') {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function restoreTokens(text, items, prefix, renderer) {
  let result = text
  items.forEach((item, index) => {
    result = result.replaceAll(`${prefix}${index}@@`, renderer(item))
  })
  return result
}

function renderInlineMarkdown(text) {
  const inlineCodes = []

  let html = text.replace(/`([^`\n]+)`/g, (_, code) => {
    const token = `${INLINE_CODE_TOKEN_PREFIX}${inlineCodes.length}@@`
    inlineCodes.push(code)
    return token
  })

  html = html.replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, (_, label, href) => {
    return `<a href="${href}" target="_blank" rel="noopener noreferrer">${label}</a>`
  })

  html = html
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/__([^_]+)__/g, '<strong>$1</strong>')
    .replace(/(^|[\s(])\*([^*\n]+)\*(?=$|[\s).,!?:;])/g, '$1<em>$2</em>')
    .replace(/(^|[\s(])_([^_\n]+)_(?=$|[\s).,!?:;])/g, '$1<em>$2</em>')

  return restoreTokens(html, inlineCodes, INLINE_CODE_TOKEN_PREFIX, (code) => `<code>${code}</code>`)
}

function renderCodeBlock(block) {
  const label = block.language ? `<div class="markdown-code-block__label">${block.language}</div>` : ''
  return [
    '<div class="markdown-code-block">',
    label,
    `<pre><code>${block.code}</code></pre>`,
    '</div>'
  ].join('')
}

function renderList(lines, ordered) {
  const pattern = ordered ? /^\d+\.\s+/ : /^[-*+]\s+/
  const items = lines
    .map((line) => line.replace(pattern, '').trim())
    .filter(Boolean)
    .map((line) => `<li>${renderInlineMarkdown(line)}</li>`)
    .join('')

  if (!items) {
    return ''
  }
  return ordered ? `<ol>${items}</ol>` : `<ul>${items}</ul>`
}

function renderQuote(lines) {
  const content = lines
    .map((line) => line.replace(/^>\s?/, '').trim())
    .filter(Boolean)
    .map((line) => `<p>${renderInlineMarkdown(line)}</p>`)
    .join('')

  return content ? `<blockquote>${content}</blockquote>` : ''
}

function renderParagraph(lines) {
  const content = lines
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => renderInlineMarkdown(line))
    .join('<br />')

  return content ? `<p>${content}</p>` : ''
}

function renderMarkdownBlock(block, codeBlocks) {
  const codeMatch = block.match(/^@@CODE_BLOCK_(\d+)@@$/)
  if (codeMatch) {
    return renderCodeBlock(codeBlocks[Number(codeMatch[1])])
  }

  if (/^(-{3,}|\*{3,}|_{3,})$/.test(block.replace(/\s+/g, ''))) {
    return '<hr />'
  }

  const headingMatch = block.match(/^(#{1,6})\s+(.+)$/)
  if (headingMatch) {
    const level = headingMatch[1].length
    return `<h${level}>${renderInlineMarkdown(headingMatch[2].trim())}</h${level}>`
  }

  const lines = block.split('\n').map((line) => line.trimEnd()).filter(Boolean)
  if (!lines.length) {
    return ''
  }

  if (lines.every((line) => /^>\s?/.test(line))) {
    return renderQuote(lines)
  }

  if (lines.every((line) => /^[-*+]\s+/.test(line))) {
    return renderList(lines, false)
  }

  if (lines.every((line) => /^\d+\.\s+/.test(line))) {
    return renderList(lines, true)
  }

  return renderParagraph(lines)
}

export function renderMarkdownToHtml(markdown = '') {
  const normalized = String(markdown).replace(/\r\n?/g, '\n').trim()
  if (!normalized) {
    return ''
  }

  const codeBlocks = []
  let text = normalized.replace(/```([\w-]+)?\n([\s\S]*?)```/g, (_, language = '', code = '') => {
    const token = `${CODE_BLOCK_TOKEN_PREFIX}${codeBlocks.length}@@`
    codeBlocks.push({
      language: escapeHtml(language.trim()),
      code: escapeHtml(code.replace(/\n$/, ''))
    })
    return `\n\n${token}\n\n`
  })

  text = escapeHtml(text)

  return text
    .split(/\n{2,}/)
    .map((block) => block.trim())
    .filter(Boolean)
    .map((block) => renderMarkdownBlock(block, codeBlocks))
    .join('')
}
