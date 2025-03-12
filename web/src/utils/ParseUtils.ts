function base64ToUtf8(ASCII_parsed_text: string) {
  const bytes = new Uint8Array(ASCII_parsed_text.length);
  for (let i = 0; i < ASCII_parsed_text.length; i++) {
    bytes[i] = ASCII_parsed_text.charCodeAt(i);
  }
  return new TextDecoder("utf-8").decode(bytes);
}

export { base64ToUtf8 };
