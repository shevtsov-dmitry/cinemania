const DEFAULT_DELIMITER = "%%SPLIT_DELIMITER%%"; // Same delimiter as at the server

/**
 * Splits binary octet content by a given delimiter.
 * 
 * @param binaryContent binary octet content splitted by delimer
 * @param delimer delimiter used to split binary octet content
 * @returns {Promise<string[]>} temporary image URLs array; each one can be inserted in the `<img src="...">`  tag to display image
 */
async function parseSplitted(binaryContent: Blob, delimer: string): Promise<string[]> {
  const reader = new FileReader();
  return new Promise((resolve, reject) => {
    reader.onload = (event) => {
      try {
        const combinedData = new Uint8Array(event.target?.result as ArrayBuffer);
        const delimiterBytes: Uint8Array = new TextEncoder().encode(delimer);
        const imageDataList = splitByDelimer(combinedData, delimiterBytes);
        const imageUrls = imageDataList.map((imageData) => {
          const blob = new Blob([imageData], { type: "image/jpeg" });
          return URL.createObjectURL(blob);
        });
        resolve(imageUrls);
      } catch (error) {
        reject(error);
      }
    };

    reader.onerror = () => {
      reject(new Error("Failed to read the blob."));
    };

    reader.readAsArrayBuffer(binaryContent);
  });
}

/**
 * Split binary content into an array of binaries based on a specified delimiter.
 * 
 * @note DEFAULT_DELIMER is exported at the file where this function located
 * 
 * @param binaryContent binary octet content splitted by delimer
 * @returns {Promise<string[]>} temporary image URLs array; each one can be inserted in the `<img src="...">`  tag to display image
 */
async function parseSplittedWithDefaultDelimiter(binaryContent: Blob): Promise<string[]> {
  return parseSplitted(binaryContent, DEFAULT_DELIMITER);
}

/**
 * Split binary content into an array of binaries based on a specified delimiter.
 *
 * @param array binary octet content splitted by delimer
 * @param delimiter delimiter byte sequence to split the binary content
 * @returns array of binary items
 */
function splitByDelimer(
  array: Uint8Array,
  delimiter: Uint8Array
): Uint8Array[] {
  const result = [];
  let start = 0;

  while (true) {
    const index = findDelimerBoundary(array, delimiter, start);
    if (index === -1) break;

    result.push(array.slice(start, index));
    start = index + delimiter.length;
  }

  if (start < array.length) {
    result.push(array.slice(start));
  }

  return result;
}

/**
 * Find the boundary of a specified delimiter in a binary array.
 *
 * @param array binary octet content
 * @param subarray delimiter byte sequence to find the boundary
 * @param start starting index for searching the delimiter
 * @returns boundary index or -1 if not found
 */
function findDelimerBoundary(array: Uint8Array, subarray: Uint8Array, start: number) {
  const maxIndex = array.length - subarray.length;
  for (let i = start; i <= maxIndex; i++) {
    let match = true;
    for (let j = 0; j < subarray.length; j++) {
      if (array[i + j] !== subarray[j]) {
        match = false;
        break;
      }
    }
    if (match) return i;
  }
  return -1;
}

export { DEFAULT_DELIMITER, parseSplitted, parseSplittedWithDefaultDelimiter };
