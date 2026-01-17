type ApiError = Error & { status?: number };

const readErrorMessage = async (response: Response) => {
  const contentType = response.headers.get("content-type") || "";
  try {
    if (contentType.includes("application/json")) {
      const data = await response.json();
      return data?.message || JSON.stringify(data);
    }
    return await response.text();
  } catch {
    return response.statusText;
  }
};

const handleAuthFailure = () => {
  localStorage.removeItem("accessToken");
  window.location.href = "/login";
};

export const apiFetch = async <T>(
  url: string,
  init: RequestInit = {}
): Promise<T> => {
  const token = localStorage.getItem("accessToken");
  const headers = new Headers(init.headers || {});

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(url, {
    ...init,
    headers,
  });

  if (response.status === 401 || response.status === 403) {
    handleAuthFailure();
  }

  if (!response.ok) {
    const message = await readErrorMessage(response);
    const error: ApiError = new Error(message || "API request failed");
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return (await response.json()) as T;
  }

  return (await response.text()) as T;
};
