export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly title: string,
    override readonly message: string,
  ) {
    super(message);
  }
}
