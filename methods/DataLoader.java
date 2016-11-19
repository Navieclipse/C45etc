package methods;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import navier.Dataset;

public class DataLoader {

	//データ読み込み改
	public static void inputFile(Dataset data, String fileName){

		List<Double[]> lines = null;
		try (Stream<String> line = Files.lines(Paths.get(fileName))) {
		    lines =
		    	line.map(s ->{
		    	String[] numbers = s.split(",");
		    	Double[] nums = new Double[numbers.length];
		    	for (int i = 0; i < nums.length; i++) {
					nums[i] = Double.parseDouble(numbers[i]);
				}
		    	return nums;
		    })
		    .collect(Collectors.toList());

		} catch (IOException e) {
		    e.printStackTrace();
		}

		//1行目はデータのパラメータ
		data.setDataSize( lines.get(0)[0].intValue() );
		data.setNdim( lines.get(0)[1].intValue() );
		data.setCnum( lines.get(0)[2].intValue() );
		lines.remove(0);

		//2行目以降は属性値（最終列はクラス）
		lines.stream().forEach(data::addPattern);
	}

}


