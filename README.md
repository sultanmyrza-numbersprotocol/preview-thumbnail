# @numbersprotocol/preview-thumbnail

The 'preview-thumbnail' plugin allows for dynamic generation of preview thumbnails from a provided URL across web, iOS, and Android platforms. This enables consistent and efficient thumbnail previews in your Capacitor applications regardless of the user's device

## Install

```bash
npm install @numbersprotocol/preview-thumbnail
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`thumbnailFile(...)`](#thumbnailfile)
* [`thumbnailData(...)`](#thumbnaildata)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### thumbnailFile(...)

```typescript
thumbnailFile(options: ThumbnailOptions) => Promise<ThumbnailFileResult>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#thumbnailoptions">ThumbnailOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#thumbnailfileresult">ThumbnailFileResult</a>&gt;</code>

--------------------


### thumbnailData(...)

```typescript
thumbnailData(options: ThumbnailOptions) => Promise<ThumbnailDataResult>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#thumbnailoptions">ThumbnailOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#thumbnaildataresult">ThumbnailDataResult</a>&gt;</code>

--------------------


### Interfaces


#### ThumbnailFileResult

| Prop        | Type                |
| ----------- | ------------------- |
| **`value`** | <code>string</code> |


#### ThumbnailOptions

| Prop                | Type                                                |
| ------------------- | --------------------------------------------------- |
| **`video`**         | <code>string</code>                                 |
| **`headers`**       | <code>{ [key: string]: string; }</code>             |
| **`thumbnailPath`** | <code>string</code>                                 |
| **`imageFormat`**   | <code><a href="#imageformat">ImageFormat</a></code> |
| **`maxHeight`**     | <code>number</code>                                 |
| **`maxWidth`**      | <code>number</code>                                 |
| **`timeMs`**        | <code>number</code>                                 |
| **`quality`**       | <code>number</code>                                 |


#### ThumbnailDataResult

| Prop        | Type                |
| ----------- | ------------------- |
| **`value`** | <code>string</code> |


### Enums


#### ImageFormat

| Members    |
| ---------- |
| **`JPEG`** |
| **`PNG`**  |
| **`WEBP`** |

</docgen-api>
