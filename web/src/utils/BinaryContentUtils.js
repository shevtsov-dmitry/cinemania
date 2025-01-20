const DEFAULT_DELIMITER = "%%SPLIT_DELIMITER%%"; // Same delimiter as server

/**
 * 
 * @param {Response} response
 * @param {string} delimiter used to separate binary content 
 * @returns {Response<string[]>} temporary image URLs array; each one can be inserted in the `<img src="...">`  tag to display image
 */
async function parseSplitted(response, delimer) {
  if (delimer === undefined) {
    throw new Error("Error parsing binary content. Delimiter is not defined.");
  }
  const blob = await response.blob();
  const reader = new FileReader();

  return new Promise((resolve, reject) => {
    reader.onload = (event) => {
      try {
        const combinedData = new Uint8Array(event.target.result);
        const delimiterBytes = new TextEncoder().encode(delimer);
        const imageDataList = splitArray(combinedData, delimiterBytes);
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

    reader.readAsArrayBuffer(blob);
  });
}

/**
 * @param {Response} response
 * @returns {Response<string[]>} temporary image URLs array; each one can be inserted in the `<img src="...">`  tag to display image
 */
async function parseSplittedWithDefaultDelimiter(binaryContent) {
  return parseSplitted(binaryContent, DEFAULT_DELIMITER);
}

function splitArray(array, delimiter) {
  const result = [];
  let start = 0;

  while (true) {
    const index = findSubarray(array, delimiter, start);
    if (index === -1) break;

    result.push(array.slice(start, index));
    start = index + delimiter.length;
  }

  if (start < array.length) {
    result.push(array.slice(start));
  }

  return result;
}

function findSubarray(array, subarray, start) {
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

export { DEFAULT_DELIMITER };
export { parseSplitted, parseSplittedWithDefaultDelimiter };
